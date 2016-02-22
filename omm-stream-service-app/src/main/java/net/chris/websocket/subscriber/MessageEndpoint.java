package net.chris.websocket.subscriber;

import java.io.IOException;

public interface MessageEndpoint<P> {

    void onMessage(Message<? extends P> payload) throws IOException;
}
