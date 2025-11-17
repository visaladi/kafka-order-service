package com.example.kafka_order_service.service;

import com.example.kafka.avro.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, Order> kafkaTemplate;
    private static final String TOPIC = "orders";

    public void sendOrder(String orderId, String product, float price) {
        Order order = Order.newBuilder()
                .setOrderId(orderId)
                .setProduct(product)
                .setPrice(price)
                .build();

        kafkaTemplate.send(TOPIC, orderId, order);
    }
}
