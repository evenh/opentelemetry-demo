package no.bekk.bekkaway.order.domain.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import no.bekk.bekkaway.order.domain.restaurant.Restaurant;

@Entity
@Table(name = "menus")
public class Menu {
  @Id
  @GeneratedValue
  private UUID id;
  private String category;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
  private Restaurant restaurant;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "menu_id")
  private List<MenuItem> menuItems;

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(final String category) {
    this.category = category;
  }

  public Restaurant getRestaurant() {
    return restaurant;
  }

  public void setRestaurant(final Restaurant restaurant) {
    this.restaurant = restaurant;
  }

  public List<MenuItem> getMenuItems() {
    return menuItems;
  }

  public void setMenuItems(final List<MenuItem> menuItems) {
    this.menuItems = menuItems;
  }

  public static Menu menuOf(String category, String... items) {
    var menu = new Menu();
    menu.setCategory(category);
    menu.setMenuItems(Arrays.stream(items).map(MenuItem::new).toList());

    return menu;
  }

  @Override
  public String toString() {
    return "Menu{" +
        "id=" + id +
        ", category='" + category + '\'' +
        ", restaurant=" + restaurant +
        '}';
  }
}
