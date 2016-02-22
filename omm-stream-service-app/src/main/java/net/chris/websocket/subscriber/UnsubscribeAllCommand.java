package net.chris.websocket.subscriber;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;

@JsonDeserialize(builder = UnsubscribeAllCommand.Builder.class)
public final class UnsubscribeAllCommand implements Command {


    private UnsubscribeAllCommand(final Builder builder) {
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
        subscriber.unsubscribeAll();
    }

    public static final class Builder {

        private Builder() {
        }

        public UnsubscribeAllCommand build() {
            return new UnsubscribeAllCommand(this);
        }
    }
}
