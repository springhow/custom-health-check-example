package com.springhow.examples.customhealthchecks;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("downstream")
public class CustomHealthIndicator implements HealthIndicator {

    @Value("${my.downstream.service.url}")
    private String downstreamUrl;

    RestTemplate restTemplate = new RestTemplate();

    @Override
    public Health health() {
        try {
            ResponseEntity<JsonNode> responseEntity
                    = restTemplate.getForEntity(downstreamUrl, JsonNode.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                String status = responseEntity.getBody().get("status").textValue();
                if (status.equals("OK")) {
                    return Health.up().withDetail("status", status).build();
                } else {
                    return Health.down().build();
                }
            } else {
                return Health.down().build();
            }
        } catch (Exception e) {
            return Health.down().withException(e).build();
        }
    }
}

