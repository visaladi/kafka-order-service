package com.example.kafka_order_service.dto;

import lombok.Data;

@Data
public class OrderRequest {
    private String orderId;
    private String product;
    private float price;
}
