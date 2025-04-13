package com.loadbalancer.loadbalancer.service;

import com.loadbalancer.loadbalancer.model.BackendServer;
import com.loadbalancer.loadbalancer.strategy.LoadBalancingStrategy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LoadBalancerService {

    void setStrategy(LoadBalancingStrategy strategy);

    void registerBackend(BackendServer server);

    void deregisterBackend(String url);

    List<BackendServer> getBackendServers();

    BackendServer selectBackend();

    ResponseEntity<String> forwardRequest(String path, HttpMethod method, HttpHeaders headers, String body);
}
