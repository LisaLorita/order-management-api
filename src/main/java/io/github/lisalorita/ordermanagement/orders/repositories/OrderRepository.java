package io.github.lisalorita.ordermanagement.orders.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.lisalorita.ordermanagement.orders.entities.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
