package no.bekk.bekkaway.order.domain.order.dto;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import no.bekk.bekkaway.order.domain.customer.Customer;

public class CreateOrderRequest {
  @NotNull(message = "Customer info required")
  private Customer customer;
  @NotEmpty(message = "An order request must have at least one order line")
  private List<OrderLineRequest> orderLines;
  @NotBlank(message = "A valid restaurant ID is required")
  private String restaurantId;

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(final Customer customer) {
    this.customer = customer;
  }

  public List<OrderLineRequest> getOrderLines() {
    return orderLines;
  }

  public void setOrderLines(final List<OrderLineRequest> orderLines) {
    this.orderLines = orderLines;
  }

  public String getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(final String restaurantId) {
    this.restaurantId = restaurantId;
  }
}

