package net.chris.websocket.subscriber;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

@JsonDeserialize(builder = UnsubscribeCommand.Builder.class)
public class UnsubscribeCommand implements Command {

    private final ImmutableList<String> ids;

    private UnsubscribeCommand(final Builder builder) {
        ids = builder.ids;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public ImmutableList<String> getIds() {
        return ids;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("ids", ids)
                .toString();
    }

    @Override
    public void execute(final Subscriber<?> subscriber) {
        subscriber.unsubscribe(this);
    }

    public static final class Builder {
        private ImmutableList<String> ids = ImmutableList.of();

        private Builder() {
        }

        public Builder withIds(final List<String> ids) {
            this.ids = ImmutableList.copyOf(ids);
            return this;
        }

        public UnsubscribeCommand build() {
            return new UnsubscribeCommand(this);
        }
    }
}
