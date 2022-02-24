package no.bekk.bekkaway.order.domain.order;

import static no.bekk.bekkaway.order.config.RabbitConfig.TOPIC_EXCHANGE_NAME;

import java.util.Collection;
import java.util.List;
import no.bekk.bekkaway.order.config.RabbitConfig.RoutingKey;
import no.bekk.bekkaway.order.domain.menu.Menu;
import no.bekk.bekkaway.order.domain.order.dto.CreateOrderRequest;
import no.bekk.bekkaway.order.domain.restaurant.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
  private static final Logger log = LoggerFactory.getLogger(OrderService.class);
  private final OrderRepository repository;
  private final RestaurantService restaurantService;
  private final RabbitTemplate rabbitTemplate;

  public OrderService(final OrderRepository orderRepository, final RestaurantService restaurantService, final RabbitTemplate rabbitTemplate) {
    this.repository = orderRepository;
    this.restaurantService = restaurantService;
    this.rabbitTemplate = rabbitTemplate;
  }

  @Transactional
  public Order createOrder(CreateOrderRequest req) {
    var restaurant = restaurantService.findById(req.getRestaurantId())
        .orElseThrow(() -> new IllegalArgumentException("Invalid restaurant ID specified"));

    log.debug("Order request for restaurant: {}", restaurant);

    var menuItems = restaurant.getMenus().stream()
        .map(Menu::getMenuItems)
        .flatMap(Collection::stream)
        .toList();

    log.debug("Restaurant has these menu items: {}", menuItems);
    log.debug("Requested order has these specified: {}", req.getOrderLines());

    var orderLines = menuItems.stream()
        .flatMap(menuItem -> req.getOrderLines().stream()
            .filter(reqLine -> menuItem.getId().equals(reqLine.getMenuItemRef()))
            .map(reqLine -> new OrderLine(menuItem, reqLine.getCount())))
        .toList();

    if (orderLines.isEmpty()) {
      throw new IllegalArgumentException("No valid order lines for order. Using the correct menu for the given restaurant?");
    }

    var order = new Order();
    order.setCustomer(req.getCustomer());
    order.setOrderLines(orderLines);
    order.setRestaurant(restaurant);

    var persistedOrder = repository.save(order);
    log.info("Persisted order: {} – shipping away to restaurant", persistedOrder);

    sendViaMq(persistedOrder, RoutingKey.RECEIVE);

    return persistedOrder;
  }

  private void sendViaMq(Order order, RoutingKey routingKey) {
    rabbitTemplate.convertAndSend(TOPIC_EXCHANGE_NAME, routingKey.name(), order);
    log.info("Shipped order {} to restaurant ", order.getId());
  }

  public List<Order> findAll() {
    return repository.findAll();
  }
}
