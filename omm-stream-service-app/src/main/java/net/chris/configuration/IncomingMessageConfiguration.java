package net.chris.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import net.chris.input.IncomingMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IncomingMessageConfiguration {

    @Autowired
    private EventBus messages;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public IncomingMessageListener incomingMessageListener() {
        return new IncomingMessageListener(objectMapper, messages);
    }
}
