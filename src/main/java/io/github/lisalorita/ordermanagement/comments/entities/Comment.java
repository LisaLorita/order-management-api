package io.github.lisalorita.ordermanagement.comments.entities;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import io.github.lisalorita.ordermanagement.orders.entities.Order;
import io.github.lisalorita.ordermanagement.users.entities.User;

@Entity
@Table(name = "comments")
public class Comment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String content;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @ManyToOne
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @ManyToOne
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  public Comment() {
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

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public User getAuthor() {
    return author;
  }

  public void setAuthor(User author) {
    this.author = author;
  }

  public Order getOrder() {
    return order;
  }

  public void setOrder(Order order) {
    this.order = order;
  }

}
