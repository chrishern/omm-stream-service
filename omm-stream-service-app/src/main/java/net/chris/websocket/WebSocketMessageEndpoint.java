package net.chris.websocket;

import java.io.IOException;

import net.chris.websocket.subscriber.Message;
import net.chris.websocket.subscriber.MessageEndpoint;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

public final class WebSocketMessageEndpoint<P> implements MessageEndpoint<WebSocketMessage<P>> {

    private final WebSocketSession session;

    public WebSocketMessageEndpoint(final WebSocketSession session) {
        this.session = session;
    }

    @Override
    public void onMessage(final Message<? extends WebSocketMessage<P>> message) throws IOException {
        session.sendMessage(message.getPayload());
    }

}
