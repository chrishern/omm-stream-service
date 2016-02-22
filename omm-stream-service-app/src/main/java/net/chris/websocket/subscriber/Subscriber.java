package net.chris.websocket.subscriber;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.eventbus.Subscribe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Subscriber<P> implements MessageEndpoint<P> {

    private static final Logger LOGGER = LogManager.getLogger();
    private Boolean subscribeAll = false;

    private Set<String> subscribedIds = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    private final MessageEndpoint<P> clientEndpoint;

    public Subscriber(final MessageEndpoint<P> clientEndpoint) {
        this.clientEndpoint = clientEndpoint;
    }

    public Boolean isSubscribedToAll(){
        return subscribeAll;
    }

    public Set<String> getSubscribedIds() {
        return subscribedIds;
    }


    @Subscribe
    public void onMessage(final Message<? extends P> message) throws IOException {

        System.out.println("Received message: " + message);

        final String id = message.getId();
        final String messageType = message.getMessageType();

        if (isOfInterestForEventUpdate(id, messageType)) {
            LOGGER.debug("forwarding message {}", message);
            clientEndpoint.onMessage(message);
        } else {
            LOGGER.debug("ignoring message {}", message);
        }
    }

    private boolean isOfInterestForEventUpdate(final String id, final String messageType) {
        return (subscribeAll || id != null && subscribedIds.contains(id));
    }

    void subscribe(final SubscribeCommand subscribe) {
        subscribedIds.addAll(subscribe.getIds());
    }

    void unsubscribe(final UnsubscribeCommand unsubscribe) {
        subscribedIds.removeAll(unsubscribe.getIds());
    }

    void subscribeAll() {
        subscribeAll = true;
    }

    void unsubscribeAll() {
        subscribeAll = false;
        subscribedIds = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    }
}