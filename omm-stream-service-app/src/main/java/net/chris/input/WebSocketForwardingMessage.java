package net.chris.input;

import com.google.common.base.MoreObjects;
import net.chris.websocket.subscriber.Message;
import org.springframework.web.socket.WebSocketMessage;

public class WebSocketForwardingMessage implements Message<WebSocketMessage<String>> {

    private final String id;
    private final String messageType;
    private final WebSocketMessage<String> payload;

    private WebSocketForwardingMessage(final Builder builder) {
        id = builder.id;
        messageType = builder.messageType;
        payload = builder.payload;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getMessageType() {
        return messageType;
    }

    @Override
    public WebSocketMessage<String> getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("messageType", messageType)
                .add("payload", payload)
                .toString();
    }

    public static final class Builder {
        private String id;
        private String messageType;
        private WebSocketMessage<String> payload;

        private Builder() {
        }

        public Builder withId(final String id) {
            this.id = id;
            return this;
        }

        public Builder withPayload(final WebSocketMessage<String> payload) {
            this.payload = payload;
            return this;
        }

        public Builder withMessageType(final String messageType) {
            this.messageType = messageType;
            return this;
        }

        public WebSocketForwardingMessage build() {
            return new WebSocketForwardingMessage(this);
        }
    }
}
