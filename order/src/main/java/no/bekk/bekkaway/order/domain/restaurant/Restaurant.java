package no.bekk.bekkaway.order.domain.restaurant;

import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import no.bekk.bekkaway.order.domain.menu.Menu;

@Entity
@Table(name = "restaurants")
public class Restaurant {
  @Id
  @GeneratedValue
  private UUID id;
  private String name;
  private String description;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "restaurant_id")
  private List<Menu> menus;

  public Restaurant() {
  }

  public Restaurant(final String name, final String description, List<Menu> menus) {
    this.name = name;
    this.description = description;
    this.menus = menus;
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

  public String getDescription() {
    return description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public List<Menu> getMenus() {
    return menus;
  }

  public void setMenus(final List<Menu> menus) {
    this.menus = menus;
  }

  @Override
  public String toString() {
    return "Restaurant{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", description='" + description + '\'' +
        '}';
  }
}
