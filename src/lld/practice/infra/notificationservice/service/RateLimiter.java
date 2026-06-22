package lld.practice.infra.notificationservice.service;

import lld.practice.infra.notificationservice.enums.Channel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Token-bucket rate limiter keyed by user+channel. Replace with Redis-backed limiter in prod.
 */
public class RateLimiter {

    private static class Bucket {
        final long capacity;
        final long refillNanosPerToken;
        final AtomicLong tokens;
        volatile long lastRefillNanos;

        Bucket(long capacity, long refillNanosPerToken) {
            this.capacity = capacity;
            this.refillNanosPerToken = refillNanosPerToken;
            this.tokens = new AtomicLong(capacity);
            this.lastRefillNanos = System.nanoTime();
        }

        synchronized boolean tryConsume() {
            refill();
            if (tokens.get() > 0) {
                tokens.decrementAndGet();
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.nanoTime();
            long elapsed = now - lastRefillNanos;
            long toAdd = elapsed / refillNanosPerToken;
            if (toAdd > 0) {
                long updated = Math.min(capacity, tokens.get() + toAdd);
                tokens.set(updated);
                lastRefillNanos = now;
            }
        }
    }

    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final long defaultCapacity;
    private final long defaultRefillNanos;

    public RateLimiter(long defaultCapacityPerMinute) {
        this.defaultCapacity = defaultCapacityPerMinute;
        this.defaultRefillNanos = (60L * 1_000_000_000L) / Math.max(1, defaultCapacityPerMinute);
    }

    public boolean allow(String userId, Channel channel) {
        String key = userId + ":" + channel;
        Bucket bucket = buckets.computeIfAbsent(key,
                k -> new Bucket(defaultCapacity, defaultRefillNanos));
        return bucket.tryConsume();
    }
}
