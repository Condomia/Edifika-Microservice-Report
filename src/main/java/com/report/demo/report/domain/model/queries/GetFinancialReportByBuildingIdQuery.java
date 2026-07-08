package com.report.demo.report.domain.model.queries;

public record GetFinancialReportByBuildingIdQuery(
        Long buildingId
) {
    public GetFinancialReportByBuildingIdQuery {
        if (buildingId == null || buildingId <= 0) {
            throw new IllegalArgumentException("Building id is required");
        }
    }
}