package com.report.demo.report.infrastructure.external.resources;

import java.time.LocalDateTime;

public record UserUnitExternalResource(
        Long id,
        Long idUserUnit,
        Long idBuilding,
        Long idUnit,
        Long idUser,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String status
) {
}