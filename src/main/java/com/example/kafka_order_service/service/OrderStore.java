package com.example.kafka_order_service.service;


import com.example.kafka.avro.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderStore {

    private final List<Order> orders = new ArrayList<>();

    public synchronized void addOrder(Order order) {
        orders.add(order);
    }

    public synchronized List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }
}
