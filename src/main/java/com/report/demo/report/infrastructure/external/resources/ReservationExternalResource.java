package com.report.demo.report.infrastructure.external.resources;

import java.time.LocalDate;

public record ReservationExternalResource(
        Long id,
        Long residentId,
        Long commonAreaId,
        LocalDate reservationDate,
        Integer timeSlot,
        Integer numberOfGuests,
        String status,
        String qrCodeAccess,
        Boolean penaltyApplied
) {
}