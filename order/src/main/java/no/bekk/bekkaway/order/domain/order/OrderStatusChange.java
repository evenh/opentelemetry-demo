package no.bekk.bekkaway.order.domain.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class OrderStatusChange {
  @JsonProperty("order_id")
  private UUID id;
  @JsonProperty("new_status")
  private OrderStatus newStatus;

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public OrderStatus getNewStatus() {
    return newStatus;
  }

  public void setNewStatus(final OrderStatus newStatus) {
    this.newStatus = newStatus;
  }

  @Override
  public String toString() {
    return "OrderStatusChange{" +
        "id='" + id + '\'' +
        ", newStatus=" + newStatus +
        '}';
  }
}
