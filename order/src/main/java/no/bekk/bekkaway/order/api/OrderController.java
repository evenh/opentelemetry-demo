package no.bekk.bekkaway.order.api;

import java.util.List;
import javax.validation.Valid;
import no.bekk.bekkaway.order.domain.order.dto.CreateOrderRequest;
import no.bekk.bekkaway.order.domain.order.Order;
import no.bekk.bekkaway.order.domain.order.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {
  private final OrderService orderService;

  public OrderController(final OrderService orderService) {
    this.orderService = orderService;
  }

  @GetMapping
  public List<Order> findAll() {
    return orderService.findAll();
  }

  @PostMapping
  public Order createOrder(@Valid @RequestBody CreateOrderRequest req) {
    return orderService.createOrder(req);
  }
}
