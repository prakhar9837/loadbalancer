package com.loadbalancer.loadbalancer.service.impl;

import com.loadbalancer.loadbalancer.config.LoadBalancerProperties;
import com.loadbalancer.loadbalancer.model.BackendServer;
import com.loadbalancer.loadbalancer.strategy.LoadBalancingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LoadBalancerServiceImplTest {

    private LoadBalancerServiceImpl loadBalancerService;
    private LoadBalancingStrategy mockStrategy;
    private WebClient mockWebClient;

    @BeforeEach
    void setUp() {
        mockStrategy = mock(LoadBalancingStrategy.class);

        // Mock WebClient and WebClient.Builder
        WebClient.Builder mockWebClientBuilder = mock(WebClient.Builder.class);
        mockWebClient = mock(WebClient.class);

        // Configure fluent builder methods
        when(mockWebClientBuilder.baseUrl(anyString())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.defaultHeader(anyString(), anyString())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.build()).thenReturn(mockWebClient);

        // Mock LoadBalancerProperties
        LoadBalancerProperties mockProperties = mock(LoadBalancerProperties.class);

        // Mock @Value injected field
        String mockedServers = "http://localhost:8090,http://localhost:8091";
        when(mockProperties.getBackendServers()).thenReturn(
                List.of(new BackendServer("http://localhost:8090", true),
                        new BackendServer("http://localhost:8091", true))
        );

        // Pass mocks to the service
        loadBalancerService = new LoadBalancerServiceImpl(mockWebClientBuilder, mockStrategy, mockProperties);
    }


    @Test
    void testRegisterBackend() {
        BackendServer server = new BackendServer("http://localhost:8087", true);
        loadBalancerService.registerBackend(server);

        List<BackendServer> servers = loadBalancerService.getBackendServers();
        assertEquals(3, servers.size());
        assertEquals("http://localhost:8087", servers.get(2).getUrl());
    }

    @Test
    void testDeregisterBackend() {
        BackendServer server = new BackendServer("http://localhost:8081", true);
        loadBalancerService.registerBackend(server);

        loadBalancerService.deregisterBackend("http://localhost:8081");
        List<BackendServer> servers = loadBalancerService.getBackendServers();
        assertTrue(servers.stream().noneMatch(s -> s.getUrl().equals("http://localhost:8081")));
    }

    @Test
    void testSelectBackend() {
        BackendServer server = new BackendServer("http://localhost:8081", true);
        loadBalancerService.registerBackend(server);

        when(mockStrategy.selectBackend(anyList())).thenReturn(server);

        BackendServer selected = loadBalancerService.selectBackend();
        assertNotNull(selected);
        assertEquals("http://localhost:8081", selected.getUrl());
    }

    @Test
    void testForwardRequest_NoBackendServers() {
        // Ensure no backend servers are registered
        loadBalancerService.getBackendServers().clear();

        HttpHeaders headers = new HttpHeaders();

        // Expect an exception due to no backend servers
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                loadBalancerService.forwardRequest("/test", HttpMethod.GET, headers, "Request Body")
        );

        assertEquals("No backend servers available.", exception.getMessage());
    }
}
