package no.bekk.bekkaway.order.domain.order.dto;

import java.util.UUID;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

public class OrderLineRequest {
  @NotEmpty(message = "Menu item UUID id must be set")
  private UUID menuItemRef;

  @Min(value = 1, message = "There must be at least 1 of each order line")
  private int count;

  public UUID getMenuItemRef() {
    return menuItemRef;
  }

  public void setMenuItemRef(final UUID menuItemRef) {
    this.menuItemRef = menuItemRef;
  }

  public int getCount() {
    return count;
  }

  public void setCount(final int count) {
    this.count = count;
  }

  @Override
  public String toString() {
    return "OrderLineRequest{" +
        "menuItemRef='" + menuItemRef + '\'' +
        ", count=" + count +
        '}';
  }
}
