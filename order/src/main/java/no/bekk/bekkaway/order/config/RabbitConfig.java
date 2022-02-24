package no.bekk.bekkaway.order.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
  public static final String TOPIC_EXCHANGE_NAME = "orders-exchange";
  public static final String TOPIC_PREFIX = "orders";
  public static final String QUEUE_ORDERS_RECEIVED = TOPIC_PREFIX + ".received";
  public static final String QUEUE_ORDERS_STATUS = TOPIC_PREFIX + ".status-updates";

  @Bean
  Queue receivedOrders() {
    return new Queue(QUEUE_ORDERS_RECEIVED, true);
  }

  @Bean
  Queue orderStatuses() {
    return new Queue(QUEUE_ORDERS_STATUS, true);
  }

  @Bean
  TopicExchange exchange() {
    return new TopicExchange(TOPIC_EXCHANGE_NAME, true, false);
  }


  @Bean
  Binding receiveBinding() {
    return BindingBuilder
        .bind(receivedOrders())
        .to(exchange())
        .with(RoutingKey.RECEIVE);
  }

  @Bean
  Binding statusBinding() {
    return BindingBuilder
        .bind(orderStatuses())
        .to(exchange())
        .with(RoutingKey.STATUS);
  }

  @Bean
  public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
    return new Jackson2JsonMessageConverter(objectMapper);
  }

  public enum RoutingKey {
    RECEIVE,
    STATUS,
  }
}
