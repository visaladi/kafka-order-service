package com.example.kafka_order_service.controller;

import com.example.kafka_order_service.dto.OrderRequest;
import com.example.kafka_order_service.service.OrderProducer;
import com.example.kafka_order_service.service.PriceAggregator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProducer orderProducer;
    private final PriceAggregator priceAggregator;

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest request) {
        orderProducer.sendOrder(request.getOrderId(), request.getProduct(), request.getPrice());
        return ResponseEntity.ok("Order sent to Kafka: " + request.getOrderId());
    }
    @GetMapping("/average")
    public float getAverage() {
        return priceAggregator.getAverage();
    }


}
