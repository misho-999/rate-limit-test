package com.demo.ratelimittest.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private static final int CAPACITY = 10; // 10 requests per 2 minutes per IP
    private static final Duration REFILL_DURATION = Duration.ofMinutes(2);

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String clientIp) {
        return buckets.computeIfAbsent(clientIp, this::createNewBucket);
    }

    private Bucket createNewBucket(String clientIp) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(CAPACITY)
                .refillGreedy(CAPACITY, REFILL_DURATION)
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}

