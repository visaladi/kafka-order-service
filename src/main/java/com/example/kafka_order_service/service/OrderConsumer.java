package com.example.kafka_order_service.service;

import com.example.kafka.avro.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderConsumer {

    private final PriceAggregator priceAggregator;
    private final KafkaTemplate<String, Order> kafkaTemplate;
    private final OrderStore orderStore;
    private static final String DLQ_TOPIC = "orders-dlq";

    @KafkaListener(topics = "orders", groupId = "order-consumer-group")
    public void consume(ConsumerRecord<String, Order> record) {
        Order order = record.value();
        String key = record.key();

        int maxRetries = 3;
        int attempts = 0;
        boolean success = false;
        // Store the order

        orderStore.addOrder(order);

        // Update running average
        priceAggregator.addPrice(order.getPrice());

        System.out.println("Consumed Order → " + order.getOrderId());
        while (attempts < maxRetries && !success) {
            try {
                attempts++;
                log.info("Processing order {} (attempt {}/{})", order.getOrderId(), attempts, maxRetries);

                // Simulate temporary failure condition test if you want
                // e.g. if (order.getPrice() < 0) throw new RuntimeException("Negative price");

                priceAggregator.addPrice(order.getPrice());
                float avg = priceAggregator.getAverage();

                log.info("Order processed: id={}, product={}, price={}, runningAvg={}",
                        order.getOrderId(), order.getProduct(), order.getPrice(), avg);

                success = true;
            } catch (Exception ex) {
                log.error("Error processing order {}: {}", order.getOrderId(), ex.getMessage());
                if (attempts >= maxRetries) {
                    log.error("Max retries reached. Sending to DLQ.");
                    sendToDlq(key, order, ex);
                } else {
                    try {
                        Thread.sleep(1000L); // backoff
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    private void sendToDlq(String key, Order order, Exception ex) {
        // You could also build a wrapper object with error info.
        kafkaTemplate.send(DLQ_TOPIC, key, order);
        log.warn("Sent order {} to DLQ topic {}", order.getOrderId(), DLQ_TOPIC);
    }
}
