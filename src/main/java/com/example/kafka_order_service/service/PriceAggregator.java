package com.example.kafka_order_service.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class PriceAggregator {

    private final AtomicLong count = new AtomicLong(0);
    private final AtomicLong sumTimes100 = new AtomicLong(0); // keep some precision

    public synchronized void addPrice(float price) {
        count.incrementAndGet();
        sumTimes100.addAndGet((long) (price * 100));
    }

    public synchronized float getAverage() {
        long c = count.get();
        if (c == 0) return 0f;
        return (sumTimes100.get() / 100.0f) / c;
    }
}
