package com.loadbalancer.loadbalancer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackendServer {
    private String url;     // e.g., http://localhost:8081

    @JsonProperty("isAlive")
    private boolean isAlive;
}
