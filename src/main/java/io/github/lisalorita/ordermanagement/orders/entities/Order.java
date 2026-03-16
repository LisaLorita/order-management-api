package io.github.lisalorita.ordermanagement.orders.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.github.lisalorita.ordermanagement.orderitems.entities.OrderItem;
import io.github.lisalorita.ordermanagement.orders.enums.OrderStatus;
import io.github.lisalorita.ordermanagement.users.entities.User;

@Entity
@Table(name = "orders")
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderStatus status = OrderStatus.PENDING;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User createdBy;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> orderItems = new ArrayList<>();

  public Order() {
  }

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void markAsProcessing() {
    this.status = OrderStatus.PROCESSING;
  }

  public void markAsShipped() {
    this.status = OrderStatus.SHIPPED;
  }

  public void markAsDelivered() {
    this.status = OrderStatus.DELIVERED;
  }

  public void markAsCancelled() {
    this.status = OrderStatus.CANCELLED;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public List<OrderItem> getOrderItems() {
    return orderItems;
  }

  public void addOrderItem(OrderItem orderItem) {
    orderItems.add(orderItem);
    orderItem.setOrder(this);
  }

  public void removeOrderItem(OrderItem orderItem) {
    orderItems.remove(orderItem);
    orderItem.setOrder(null);
  }

  public BigDecimal getTotalAmount() {
    if (orderItems.isEmpty()) {
      return BigDecimal.ZERO;
    }

    BigDecimal total = BigDecimal.ZERO;

    for (OrderItem item : orderItems) {
      BigDecimal itemTotal = item.getPurchasePrice()
          .multiply(BigDecimal.valueOf(item.getQuantity()));

      total = total.add(itemTotal);
    }

    return total;
  }

}