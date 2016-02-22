package net.chris.websocket.subscriber;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;

@JsonDeserialize(builder = SubscribeAllCommand.Builder.class)
public final class SubscribeAllCommand implements Command {


    private SubscribeAllCommand(final Builder builder) {
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .toString();
    }

    @Override
    public void execute(final Subscriber<?> subscriber) {
        subscriber.subscribeAll();
    }

    public static final class Builder {

        private Builder() {
        }

        public SubscribeAllCommand build() {
            return new SubscribeAllCommand(this);
        }
    }
}
