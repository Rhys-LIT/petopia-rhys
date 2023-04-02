package com.example.assignmenttwo_starter.services;

import com.example.assignmenttwo_starter.model.Order;
import com.example.assignmenttwo_starter.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Page<Order> findAll(PageRequest pageRequest) {
        return orderRepository.findAll(pageRequest);
    }

    public Optional<Order> findById(Integer orderId) {
        return orderRepository.findById(orderId);
    }

}


