package net.chris.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.eventbus.EventBus;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class Application {

	@Bean
	public ActiveMQTopic oneMinuteMarketsTopic() {
	    return new ActiveMQTopic("minuteMarketsUpdateTopic");
	}
	
	@Bean
	public ObjectMapper objectMapper() {
		final ObjectMapper objectMapper = new ObjectMapper();

		objectMapper.registerModule(new JodaModule());

		return objectMapper;
	}

	@Bean
	public EventBus messages() {
		return new EventBus();
	}

	public static void main (final String [] args) {
		SpringApplication.run(Application.class, args);
	}
}
