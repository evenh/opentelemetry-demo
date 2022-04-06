package no.bekk.bekkaway.order.api;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import no.bekk.bekkaway.order.domain.order.dto.CreateOrderRequest;
import no.bekk.bekkaway.order.domain.order.Order;
import no.bekk.bekkaway.order.domain.order.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

  @GetMapping("/{id}")
  public Order findOne(@PathVariable("id") @NotNull String id) {
    return orderService.findOne(id).orElseThrow(IllegalArgumentException::new);
  }

  @PostMapping
  public Order createOrder(@Valid @RequestBody CreateOrderRequest req) {
    return orderService.createOrder(req);
  }
}
