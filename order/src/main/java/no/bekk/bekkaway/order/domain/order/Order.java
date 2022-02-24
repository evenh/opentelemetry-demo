package no.bekk.bekkaway.order.domain.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import no.bekk.bekkaway.order.domain.customer.Customer;
import no.bekk.bekkaway.order.domain.restaurant.Restaurant;
import org.springframework.lang.NonNull;

@Entity
@Table(name = "orders")
public class Order {
  @Id
  @GeneratedValue
  private UUID id;

  @NonNull
  @Embedded
  private Customer customer;

  @NonNull
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @ManyToOne
  @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
  private Restaurant restaurant;

  @NonNull
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "order_id")
  private List<OrderLine> orderLines;

  private ZonedDateTime orderedAt = ZonedDateTime.now();

  private OrderStatus status = OrderStatus.RECEIVED;

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(final Customer customer) {
    this.customer = customer;
  }

  public Restaurant getRestaurant() {
    return restaurant;
  }

  public void setRestaurant(final Restaurant restaurant) {
    this.restaurant = restaurant;
  }

  public List<OrderLine> getOrderLines() {
    return orderLines;
  }

  public void setOrderLines(final List<OrderLine> orderLines) {
    this.orderLines = orderLines;
  }

  public ZonedDateTime getOrderedAt() {
    return orderedAt;
  }

  public void setOrderedAt(final ZonedDateTime orderedAt) {
    this.orderedAt = orderedAt;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(final OrderStatus status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return "Order{" +
        "id=" + id +
        ", customer=" + customer +
        ", restaurant=" + restaurant +
        ", orderLines=" + orderLines +
        ", orderedAt=" + orderedAt +
        ", status=" + status +
        '}';
  }
}
