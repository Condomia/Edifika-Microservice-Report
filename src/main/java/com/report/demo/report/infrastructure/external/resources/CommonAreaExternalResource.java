package com.report.demo.report.infrastructure.external.resources;

public record CommonAreaExternalResource(
        Long id,
        String name,
        String type,
        String status,
        Integer maxCapacity,
        String bookingType,
        CommonAreaRulesExternalResource rules
) {
}