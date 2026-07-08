package com.report.demo.report.infrastructure.external.clients;

import com.report.demo.report.infrastructure.external.resources.ReservationExternalResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class ReservationClient {

    private final WebClient webClient;

    public ReservationClient(
            WebClient.Builder webClientBuilder,
            @Value("${external.services.reservation-url}") String reservationUrl
    ) {
        this.webClient = webClientBuilder
                .baseUrl(reservationUrl)
                .build();
    }

    public List<ReservationExternalResource> getAllReservations() {
        return webClient.get()
                .uri("/api/v1/reservations")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ReservationExternalResource>>() {})
                .block();
    }
}