package com.loadbalancer.loadbalancer.controller;

import com.loadbalancer.loadbalancer.model.BackendServer;
import com.loadbalancer.loadbalancer.service.LoadBalancerService;
import com.loadbalancer.loadbalancer.strategy.impl.RandomStrategy;
import com.loadbalancer.loadbalancer.strategy.impl.RoundRobinStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loadbalancer")
public class LoadBalancerController {

    private final LoadBalancerService loadBalancerService;

    @Autowired
    public LoadBalancerController(LoadBalancerService loadBalancerService) {
        this.loadBalancerService = loadBalancerService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerBackend(@RequestBody BackendServer server) {
        loadBalancerService.registerBackend(server);
        return ResponseEntity.ok("Backend server registered successfully.");
    }

    @DeleteMapping("/deregister")
    public ResponseEntity<String> deregisterBackend(@RequestParam String url) {
        loadBalancerService.deregisterBackend(url);
        return ResponseEntity.ok("Backend server deregistered successfully.");
    }

    @GetMapping("/strategy")
    public ResponseEntity<String> setLoadBalancingStrategy(@RequestParam String strategyName) {
        switch (strategyName.toLowerCase()) {
            case "roundrobin":
                loadBalancerService.setStrategy(new RoundRobinStrategy());
                break;
            case "random":
                loadBalancerService.setStrategy(new RandomStrategy());
                break;
            default:
                return ResponseEntity.badRequest().body("Invalid strategy name.");
        }
        return ResponseEntity.ok("Load balancing strategy set to " + strategyName);
    }

    @GetMapping("/server/all")
    public ResponseEntity<?> getBackendServers() {
        return ResponseEntity.ok(loadBalancerService.getBackendServers());
    }

    @RequestMapping(value = "/forward/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<String> forwardRequest(
            @RequestHeader HttpHeaders headers,
            @RequestBody(required = false) String body,
            HttpMethod method,
            @RequestParam String path) {

        if (path == null || path.isEmpty()) {
            return ResponseEntity.badRequest().body("Path parameter is required.");
        }

        return loadBalancerService.forwardRequest(path, method, headers, body);
    }
}