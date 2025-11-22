package com.example.kafka_order_service.service;

import com.example.kafka.avro.Order;
import com.example.kafka_order_service.dto.OrderView;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class OrderStore {

    // We store simple DTOs that are easy to serialize to JSON
    private final List<OrderView> orders = new CopyOnWriteArrayList<>();

    // Called from OrderConsumer with Avro Order
    public void addOrder(Order avroOrder) {
        OrderView view = new OrderView(
                avroOrder.getOrderId().toString(),
                avroOrder.getProduct().toString(),
                avroOrder.getPrice()
        );
        orders.add(view);
    }

    // Used by the controller
    public List<OrderView> getAll() {
        return orders;
    }

    // (Optional) If you ever want the old name too:
    // public List<OrderView> getAllOrders() {
    //     return orders;
    // }
}