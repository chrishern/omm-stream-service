package net.chris.websocket.subscriber;

public interface Message<P> {

    String getId();

    String getMessageType();

    P getPayload();
}
