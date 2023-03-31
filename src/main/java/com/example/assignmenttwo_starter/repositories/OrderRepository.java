package com.example.assignmenttwo_starter.repositories;

import com.example.assignmenttwo_starter.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
}

