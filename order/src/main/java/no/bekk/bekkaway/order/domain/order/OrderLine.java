package no.bekk.bekkaway.order.domain.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import no.bekk.bekkaway.order.domain.menu.MenuItem;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "order_line")
public class OrderLine {
  @Id
  @GeneratedValue
  private UUID id;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "order_id", referencedColumnName = "id")
  private Order order;

  @NotNull
  @OneToOne
  private MenuItem menuItem;

  @Min(value = 1, message = "There must be at least 1 of each order line")
  private int count;

  public OrderLine() {
  }

  public OrderLine(final MenuItem menuItem, final int count) {
    this.menuItem = menuItem;
    this.count = count;
  }

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public Order getOrder() {
    return order;
  }

  public void setOrder(final Order order) {
    this.order = order;
  }

  public MenuItem getMenuItem() {
    return menuItem;
  }

  public void setMenuItem(final MenuItem menuItem) {
    this.menuItem = menuItem;
  }

  public int getCount() {
    return count;
  }

  public void setCount(final int count) {
    this.count = count;
  }

  @Override
  public String toString() {
    return "OrderLine{" +
        "id=" + id +
        ", order=" + order +
        ", menuItem=" + menuItem +
        ", count=" + count +
        '}';
  }
}
