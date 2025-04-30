package com.example.mainbot.repository;

import com.example.mainbot.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByShopId(Long shopId);
    List<Order> findByCustomerId(Long customerId);
}
