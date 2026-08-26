package com.priyankaa.enterprise_order_management_system.repository;

import com.priyankaa.enterprise_order_management_system.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId); // Helpful for a user tracking their purchase history
}
