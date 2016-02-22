package net.chris.input;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import net.chris.api.caerus.output.CaerusOutput;
import net.chris.websocket.subscriber.Message;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;

public class IncomingMessageListener {

    private ObjectMapper objectMapper;
    private EventBus eventBus;

    public IncomingMessageListener(final ObjectMapper objectMapper, final EventBus eventBus) {
        this.objectMapper = objectMapper;
        this.eventBus = eventBus;
    }

    @JmsListener(destination = "minuteMarketsUpdateTopic")
    public void receiveMessage(final String message) throws JsonParseException, JsonMappingException, IOException {
        System.out.println("MESSAGE RECEIVED: " + message);

        final CaerusOutput caerusOutput = objectMapper.readValue(message, CaerusOutput.class);

        System.out.println("SUCCESSFULLY READ MESSAGE");

        final Message<WebSocketMessage<String>> forwardingMessage = WebSocketForwardingMessage.newBuilder()
                .withId("")
                .withMessageType("")
                .withPayload(new TextMessage(message))
                .build();

        eventBus.post(forwardingMessage);

        System.out.println("POSTED TO SOCKED SUBSCRIBERS");
    }
}
