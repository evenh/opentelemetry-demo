package no.bekk.bekkaway.order.domain.restaurant;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RestaurantService {
  private static final Logger log = LoggerFactory.getLogger(RestaurantService.class);
  private final RestaurantRepository repository;

  public RestaurantService(final RestaurantRepository restaurantRepository) {
    this.repository = Objects.requireNonNull(restaurantRepository);
  }

  /**
   * Inserts restaurants.
   *
   * @return The actual restaurants created.
   */
  @Transactional
  public List<Restaurant> createRestaurantsIfNotExists(List<Restaurant> restaurants) {
    return restaurants.stream()
        .filter(r -> !repository.existsByName(r.getName()))
        .map(repository::save)
        .peek(r -> log.info("Inserted restaurant {}", r))
        .toList();
  }

  public List<Restaurant> findAll() {
    log.info("Listing all restaurants");
    return repository.findAll();
  }

  public Optional<Restaurant> findById(String id) {
    return findById(UUID.fromString(id));
  }

  public Optional<Restaurant> findById(UUID id) {
    return repository.findById(id);
  }
}
