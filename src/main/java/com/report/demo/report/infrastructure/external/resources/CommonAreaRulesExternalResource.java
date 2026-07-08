package com.report.demo.report.infrastructure.external.resources;

import java.math.BigDecimal;

public record CommonAreaRulesExternalResource(
        Integer maxReservationHours,
        Boolean requiresPayment,
        BigDecimal price,
        Boolean requiresGuarantee,
        BigDecimal guaranteeAmount,
        Boolean allowCancellation,
        Integer penaltyHoursBefore,
        BigDecimal penaltyAmount,
        Boolean requiresApproval
) {
}