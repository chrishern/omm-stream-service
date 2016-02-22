package net.chris.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import net.chris.websocket.SubscriptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventBus eventBus;

    @Override
    public void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {
        registry
                .addHandler(subscriptionHandler(), "/eventUpdates")
                .setAllowedOrigins("*")
        ;
        registry
                .addHandler(subscriptionHandler(), "/eventUpdates/sockjs")
                .setAllowedOrigins("*")
                .withSockJS()
        ;

    }

    @Bean
    public SubscriptionHandler subscriptionHandler() {

        return SubscriptionHandler.newBuilder()
                .withObjectMapper(objectMapper)
                .withMessages(eventBus)
                .withMaxConnectionLimit(10  )
                .build();
    }
}
