package com.loadbalancer.loadbalancer.strategy.impl;

import com.loadbalancer.loadbalancer.model.BackendServer;
import com.loadbalancer.loadbalancer.strategy.LoadBalancingStrategy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Primary
public class RoundRobinStrategy implements LoadBalancingStrategy {
    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    public BackendServer selectBackend(List<BackendServer> backends) {
        if (backends.isEmpty()) return null;
        int i = Math.abs(index.getAndIncrement() % backends.size());
        return backends.get(i);
    }
}