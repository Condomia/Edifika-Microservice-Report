package com.report.demo.report.infrastructure.external.clients;

import com.report.demo.report.infrastructure.external.resources.UnitExternalResource;
import com.report.demo.report.infrastructure.external.resources.UserUnitExternalResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class ResidentialClient {

    private final WebClient webClient;

    public ResidentialClient(
            WebClient.Builder webClientBuilder,
            @Value("${external.services.residential-url}") String residentialUrl
    ) {
        this.webClient = webClientBuilder
                .baseUrl(residentialUrl)
                .build();
    }

    public List<UnitExternalResource> getUnitsByBuildingId(Long buildingId) {
        return webClient.get()
                .uri("/api/v1/residential/buildings/{buildingId}/units", buildingId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UnitExternalResource>>() {})
                .block();
    }

    public List<UserUnitExternalResource> getResidentsByBuildingId(Long buildingId) {
        return webClient.get()
                .uri("/api/v1/residential/buildings/{buildingId}/residents", buildingId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserUnitExternalResource>>() {})
                .block();
    }
}