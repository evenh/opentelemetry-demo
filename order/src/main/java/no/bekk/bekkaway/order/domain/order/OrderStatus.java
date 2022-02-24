package no.bekk.bekkaway.order.domain.order;

public enum OrderStatus {
  RECEIVED,
  SENT_TO_RESTAURANT,
  ACCEPTED_BY_RESTAURANT,
  MAKING_FOOD,
  OUT_FOR_DELIVERY,
  DELIVERED,
  ERROR,
}
