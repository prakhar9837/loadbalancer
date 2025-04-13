package com.loadbalancer.loadbalancer.strategy.impl;

import com.loadbalancer.loadbalancer.model.BackendServer;
import com.loadbalancer.loadbalancer.strategy.LoadBalancingStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class RandomStrategy implements LoadBalancingStrategy {
    private final Random random = new Random();

    @Override
    public BackendServer selectBackend(List<BackendServer> backends) {
        if (backends.isEmpty()) return null;
        return backends.get(random.nextInt(backends.size()));
    }
}
