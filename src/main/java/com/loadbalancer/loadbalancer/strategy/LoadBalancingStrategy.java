package com.loadbalancer.loadbalancer.strategy;

import com.loadbalancer.loadbalancer.model.BackendServer;

import java.util.List;

public interface LoadBalancingStrategy {
    BackendServer selectBackend(List<BackendServer> backends);
}
