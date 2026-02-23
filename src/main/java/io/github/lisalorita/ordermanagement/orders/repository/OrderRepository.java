package io.github.lisalorita.ordermanagement.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.lisalorita.ordermanagement.orders.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
