package no.bekk.bekkaway.order.domain.restaurant;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
  /**
   * Check whether a restaurant by the given name exists.
   */
  boolean existsByName(String name);
}
