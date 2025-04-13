package com.loadbalancer.loadbalancer.service.impl;

import com.loadbalancer.loadbalancer.config.LoadBalancerProperties;
import com.loadbalancer.loadbalancer.model.BackendServer;
import com.loadbalancer.loadbalancer.service.LoadBalancerService;
import com.loadbalancer.loadbalancer.strategy.LoadBalancingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class LoadBalancerServiceImpl implements LoadBalancerService {

    private final List<BackendServer> backendServers = new CopyOnWriteArrayList<>();
    private LoadBalancingStrategy strategy;
    private final WebClient webClient;

    @Autowired
    public LoadBalancerServiceImpl(WebClient.Builder webClientBuilder,
                                   LoadBalancingStrategy loadBalancingStrategy,
                                   LoadBalancerProperties properties) {
        this.webClient = webClientBuilder.build();
        this.strategy = loadBalancingStrategy; // Set default strategy
        this.backendServers.addAll(properties.getBackendServers());
    }

    @Override
    public void setStrategy(LoadBalancingStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public void registerBackend(BackendServer server) {
        boolean alreadyRegistered = backendServers.stream()
                .anyMatch(backend -> backend.getUrl().equalsIgnoreCase(server.getUrl()));

        if (alreadyRegistered) {
            throw new IllegalArgumentException("Server is already registered.");
        }

        backendServers.add(server);
    }

    @Override
    public void deregisterBackend(String url) {
        boolean serverExists = backendServers.stream()
                .anyMatch(server -> server.getUrl().equalsIgnoreCase(url));

        if (!serverExists) {
            throw new IllegalArgumentException("Server with URL " + url + " not found.");
        }

        backendServers.removeIf(server -> server.getUrl().equalsIgnoreCase(url));
    }

    @Override
    public List<BackendServer> getBackendServers() {
        return backendServers;
    }

    @Override
    public BackendServer selectBackend() {
        if (backendServers.isEmpty()) {
            throw new IllegalStateException("No backend servers available.");
        }
        return strategy.selectBackend(backendServers);
    }

    @Override
    public ResponseEntity<String> forwardRequest(String path,
                                                 HttpMethod method,
                                                 HttpHeaders headers,
                                                 String body) {

        BackendServer selected = selectBackend();
        String fullUrl = selected.getUrl() + path;

        // Send a temporary response immediately
        ResponseEntity<String> temporaryResponse = ResponseEntity.accepted()
                .body("Request is successfully forwarded to the backend server: " + selected.getUrl());

        webClient.method(method)
                .uri(fullUrl)
                .headers(h -> h.addAll(headers))
                .bodyValue(body)
                .retrieve()
                .toEntity(String.class)
                .subscribe(response -> {
                    // Handle the backend response if needed
                    System.out.println("Backend response: " + response.getBody());
                }, error -> {
                    // Handle errors if the backend request fails
                    System.err.println("Failed to forward request: " + error.getMessage());
                });

        return temporaryResponse;
    }
}
