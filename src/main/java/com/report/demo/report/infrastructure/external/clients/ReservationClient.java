package com.report.demo.report.infrastructure.external.clients;

import com.report.demo.report.infrastructure.external.resources.ReservationExternalResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class ReservationClient {

    private final WebClient webClient;

    public ReservationClient(
            WebClient.Builder webClientBuilder,
            @Value("${external.services.api-gateway-url}") String apiGatewayUrl
    ) {
        this.webClient = webClientBuilder
                .baseUrl(apiGatewayUrl)
                .build();
    }

    public List<ReservationExternalResource> getAllReservations() {
        return webClient.get()
                .uri("/api/v1/reservations")
                .headers(headers -> addAuthorizationHeader(headers))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ReservationExternalResource>>() {})
                .block();
    }

    private void addAuthorizationHeader(HttpHeaders headers) {
        var attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return;
        }

        String authorization = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization != null) {
            headers.set(HttpHeaders.AUTHORIZATION, authorization);
        }
    }
}