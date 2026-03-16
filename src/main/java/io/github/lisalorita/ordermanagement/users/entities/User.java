package io.github.lisalorita.ordermanagement.users.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import io.github.lisalorita.ordermanagement.comments.entities.Comment;
import io.github.lisalorita.ordermanagement.orders.entities.Order;

@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "createdBy")
  private List<Order> ordersCreated = new ArrayList<>();

  @OneToMany(mappedBy = "author")
  private List<Comment> commentsCreated = new ArrayList<>();

  public User() {
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public List<Order> getOrders() {
    return ordersCreated;
  }

  public void addOrder(Order order) {
    ordersCreated.add(order);
    order.setCreatedBy(this);
  }

  public List<Comment> getComments() {
    return commentsCreated;
  }

  public void addComment(Comment comment) {
    commentsCreated.add(comment);
    comment.setAuthor(this);
  }

}