package no.bekk.bekkaway.order.api;

import java.util.List;
import no.bekk.bekkaway.order.domain.restaurant.Restaurant;
import no.bekk.bekkaway.order.domain.restaurant.RestaurantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
  private final RestaurantService restaurantService;

  public RestaurantController(final RestaurantService restaurantService) {
    this.restaurantService = restaurantService;
  }

  @GetMapping
  public List<Restaurant> findAll() {
    return restaurantService.findAll();
  }
}
