package com.report.demo.report.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.List;

public record FinancialReportResource(
        Long buildingId,
        BigDecimal totalDebt,
        BigDecimal totalOverdueDebt,
        BigDecimal totalCollectedFromDebts,
        BigDecimal collectionRate,
        BigDecimal overdueRate,
        BigDecimal totalCollectedFromReservations,
        List<AreaFinancialReportResource> areas
) {
}