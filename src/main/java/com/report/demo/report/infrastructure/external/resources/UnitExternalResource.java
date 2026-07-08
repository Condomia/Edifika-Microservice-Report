package com.report.demo.report.infrastructure.external.resources;

import java.math.BigDecimal;

public record UnitExternalResource(
        Long id,
        Long idUnit,
        Long idBuilding,
        String unitNumber,
        Integer floor,
        BigDecimal coveredArea,
        BigDecimal totalArea,
        BigDecimal participationPercentage,
        BigDecimal distributionPercentage,
        String status
) {
}