package com.understandingjava.iws_app.Repos;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class RateLimiterRepo {

    private final ConcurrentHashMap<String, Deque<Instant>> store = new ConcurrentHashMap<>();

    public int recordAndCount(String ip, long windowSeconds) {
        Instant now    = Instant.now();
        Instant cutoff = now.minusSeconds(windowSeconds);

        Deque<Instant> hits = store.computeIfAbsent(ip, k -> new ArrayDeque<>());

        synchronized (hits) {
            while (!hits.isEmpty() && hits.peekFirst().isBefore(cutoff)) {
                hits.pollFirst();
            }
            hits.addLast(now);
            return hits.size();
        }
    }

    public void reset(String ip) {
        store.remove(ip);
    }
}