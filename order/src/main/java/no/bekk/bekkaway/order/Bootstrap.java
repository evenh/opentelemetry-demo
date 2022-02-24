package no.bekk.bekkaway.order;

import static no.bekk.bekkaway.order.domain.menu.Menu.menuOf;

import java.util.List;
import no.bekk.bekkaway.order.domain.restaurant.Restaurant;
import no.bekk.bekkaway.order.domain.restaurant.RestaurantService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class Bootstrap implements ApplicationListener<ApplicationReadyEvent> {
  private final RestaurantService restaurantService;
  private final List<Restaurant> restaurantsToCreate;

  public Bootstrap(final RestaurantService restaurantService) {
    this.restaurantService = restaurantService;

    // Peppes Pizza
    var peppes = new Restaurant("Peppes Pizza", "Meetup Food", List.of(
        menuOf("Italian", "Capricciosa", "Nduja", "Calzone"),
        menuOf("American", "Pig's Knuckle", "Pepper Steak", "The Tropical")
    ));

    var illegalBurger = new Restaurant("Illegal Burger Grünerløkka", "Best burgers in town", List.of(
        menuOf("Burgers", "Hot Mama", "Hot Mama Deluxe", "Greenchillisalsa"),
        menuOf("Extras", "French Fries", "Sweet Potato Fries", "Bacon")
    ));

    restaurantsToCreate = List.of(peppes, illegalBurger);
  }

  @Transactional
  @Override
  public void onApplicationEvent(final ApplicationReadyEvent event) {
    restaurantService.createRestaurantsIfNotExists(restaurantsToCreate);
  }
}
