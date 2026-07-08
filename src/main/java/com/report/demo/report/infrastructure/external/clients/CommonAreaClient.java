package com.report.demo.report.infrastructure.external.clients;

import com.report.demo.report.infrastructure.external.resources.CommonAreaExternalResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class CommonAreaClient {

    private final WebClient webClient;

    public CommonAreaClient(
            WebClient.Builder webClientBuilder,
            @Value("${external.services.reservation-url}") String reservationUrl
    ) {
        this.webClient = webClientBuilder
                .baseUrl(reservationUrl)
                .build();
    }

    public List<CommonAreaExternalResource> getAllCommonAreas() {
        return webClient.get()
                .uri("/api/v1/common-areas")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CommonAreaExternalResource>>() {})
                .block();
    }
}