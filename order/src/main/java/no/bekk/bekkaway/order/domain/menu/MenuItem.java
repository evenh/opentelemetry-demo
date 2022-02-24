package no.bekk.bekkaway.order.domain.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "menu_item")
public class MenuItem {
  @Id
  @GeneratedValue
  private UUID id;
  private String name;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "menu_id", referencedColumnName = "id")
  private Menu menu;

  public MenuItem() {
  }

  public MenuItem(final String name) {
    this.name = name;
  }

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public Menu getMenu() {
    return menu;
  }

  public void setMenu(final Menu menu) {
    this.menu = menu;
  }

  @Override
  public String toString() {
    return "MenuItem{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", menu=" + menu +
        '}';
  }
}
