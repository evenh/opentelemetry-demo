package no.bekk.bekkaway.order.domain.order;

import static no.bekk.bekkaway.order.config.RabbitConfig.QUEUE_ORDERS_STATUS;
import static org.springframework.amqp.support.AmqpHeaders.DELIVERY_TAG;

import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderStatusProcessor {
  private static final Logger log = LoggerFactory.getLogger(OrderStatusProcessor.class);
  private final OrderRepository orderRepository;

  public OrderStatusProcessor(final OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Transactional
  @RabbitListener(queues = QUEUE_ORDERS_STATUS, ackMode = "MANUAL")
  public void onStatusChange(final OrderStatusChange change, Channel channel, @Header(DELIVERY_TAG) long tag) {
    Optional<Order> updatedOrder = orderRepository
        .findById(change.getId())
        .map(o -> {
          o.setStatus(change.getNewStatus());
          return o;
        })
        .map(orderRepository::save);

    updatedOrder.ifPresent(order -> {
      log.info("Successfully updated order {} with new status: {}", order.getId(), order.getStatus());
      try {
        channel.basicAck(tag, false);
      } catch (IOException e) {
        log.warn("Could not ack status update", e);
      }
    });
  }
}
