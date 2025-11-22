package com.example.kafka_order_service.service;


import com.example.kafka.avro.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, Order> kafkaTemplate;

    @Value("${app.kafka.order-topic}")
    private String orderTopic;

    public void sendOrder(String orderId, String product, float price) {
        Order order = Order.newBuilder()
                .setOrderId(orderId)
                .setProduct(product)
                .setPrice(price)
                .build();

        ProducerRecord<String, Order> record =
                new ProducerRecord<>(orderTopic, orderId, order);

        log.info("Sending order to Kafka: {}", order);
        kafkaTemplate.send(record);
    }
}
