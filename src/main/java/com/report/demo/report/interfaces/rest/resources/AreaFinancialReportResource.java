package com.report.demo.report.interfaces.rest.resources;

import java.math.BigDecimal;

public record AreaFinancialReportResource(
        Long areaId,
        String areaName,
        BigDecimal totalCollected,
        BigDecimal totalPenalties,
        Integer totalReservations
) {
}