package com.loadbalancer.loadbalancer.config;

import com.loadbalancer.loadbalancer.model.BackendServer;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class LoadBalancerProperties {

    @Value("${loadbalancer.servers}")
    private String servers;

    private final List<BackendServer> backendServers = new ArrayList<>();

    @PostConstruct
    public void initializeServers() {
        for (String url : servers.split(",")) {
            backendServers.add(new BackendServer(url.trim(), true));
        }
    }

    public List<BackendServer> getBackendServers() {
        return backendServers;
    }
}