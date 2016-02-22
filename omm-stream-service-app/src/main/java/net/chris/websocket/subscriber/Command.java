package net.chris.websocket.subscriber;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = NAME, property = "command")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "subscribe", value = SubscribeCommand.class),
        @JsonSubTypes.Type(name = "unsubscribe", value = UnsubscribeCommand.class),
        @JsonSubTypes.Type(name = "subscribeAll", value = SubscribeAllCommand.class),
        @JsonSubTypes.Type(name = "unsubscribeAll", value = UnsubscribeAllCommand.class)
})
public interface Command {

    void execute(Subscriber<?> subscriber);

}
