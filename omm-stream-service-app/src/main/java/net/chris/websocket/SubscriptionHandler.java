package net.chris.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import net.chris.websocket.subscriber.Command;
import net.chris.websocket.subscriber.MessageEndpoint;
import net.chris.websocket.subscriber.Subscriber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class SubscriptionHandler extends TextWebSocketHandler {

    private static final Logger LOGGER = LogManager.getLogger();
    private final ObjectMapper objectMapper;
    private final EventBus messages;
    private final int maxWaitingTimeMillis;
    private final int maxWaitingSizeBytes;
    private final int maxConnectionLimit;
    List<Subscriber<WebSocketMessage<String>>> subscribers = new ArrayList<>();

    private SubscriptionHandler(final Builder builder) {
        objectMapper = builder.objectMapper;
        messages = builder.messages;
        maxWaitingTimeMillis = builder.maxWaitingTimeMillis;
        maxWaitingSizeBytes = builder.maxWaitingSizeBytes;
        maxConnectionLimit = builder.maxConnectionLimit;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    private static Subscriber<?> subscriberFor(final WebSocketSession session) {
        return (Subscriber<?>) session.getAttributes().get("subscriber");
    }

    public List<Subscriber<WebSocketMessage<String>>> getSubscribers() {
        return Collections.unmodifiableList(subscribers);
    }

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws IOException {
        String logMessage = String.format("connection established '%s', ip:'%s'", session, session.getRemoteAddress());
        System.out.println(logMessage);
        final Subscriber<WebSocketMessage<String>> subscriber = newSubscriber(session);
        session.getAttributes().put("subscriber", subscriber);
        messages.register(subscriber);
        subscribers.add(subscriber);
        if (this.subscribers.size() > this.maxConnectionLimit) {
            session.close(CloseStatus.SERVICE_OVERLOAD);
        }
    }

    private Subscriber<WebSocketMessage<String>> newSubscriber(final WebSocketSession session) {
        final ConcurrentWebSocketSessionDecorator decoratedConcurrentSession = new ConcurrentWebSocketSessionDecorator(session, maxWaitingTimeMillis, maxWaitingSizeBytes);
        final MessageEndpoint<WebSocketMessage<String>> clientEndpoint = new WebSocketMessageEndpoint<>(decoratedConcurrentSession);
        return new Subscriber<>(clientEndpoint);
    }

    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) {
        System.out.println(String.format("connection closed %s %s ip: %s", status, session, session.getRemoteAddress()));
        final Subscriber<?> subscriber = subscriberFor(session);
        if (subscriber != null) {
            try {
                messages.unregister(subscriber);
            } catch (IllegalArgumentException e) {
                // just continue - messages.unregister(Object) throws IAE if subscriber is not registered
            } finally {
                subscribers.remove(subscriber);
            }
        }
    }

    @Override
    protected void handleTextMessage(final WebSocketSession session,
                                     final TextMessage message) throws IOException {

        final String payload = message.getPayload();
        System.out.println(String.format("received %s from %s, ip: %s", payload, session, session.getRemoteAddress()));
        final Command command;
        try {
            command = objectMapper.readValue(payload, Command.class);
        } catch (final IOException e) {
            LOGGER.info("client {} sent unparseable message {}", session.getRemoteAddress(), message);
            session.close(CloseStatus.BAD_DATA.withReason("cannot parse as JSON"));
            return;
        }

        command.execute(subscriberFor(session));
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        super.handlePongMessage(session, message);
        LOGGER.debug("received heartbeat from client.  Message: {} from {} ip: {}", message, session, session.getRemoteAddress());
    }

    public static final class Builder {
        private ObjectMapper objectMapper;
        private EventBus messages;
        private int maxWaitingTimeMillis;
        private int maxWaitingSizeBytes;
        private int maxConnectionLimit;

        private Builder() {
        }

        public Builder withObjectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        public Builder withMessages(EventBus messages) {
            this.messages = messages;
            return this;
        }

        public Builder withMaxWaitingTimeMillis(int maxWaitingTimeMillis) {
            this.maxWaitingTimeMillis = maxWaitingTimeMillis;
            return this;
        }

        public Builder withMaxWaitingSizeBytes(int maxWaitingSizeBytes) {
            this.maxWaitingSizeBytes = maxWaitingSizeBytes;
            return this;
        }

        public SubscriptionHandler build() {
            return new SubscriptionHandler(this);
        }

        public Builder withMaxConnectionLimit(int maxConnectionLimit) {
            this.maxConnectionLimit = maxConnectionLimit;
            return this;
        }
    }
}