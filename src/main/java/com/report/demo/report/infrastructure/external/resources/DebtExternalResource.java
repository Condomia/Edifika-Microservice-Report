package com.report.demo.report.infrastructure.external.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DebtExternalResource(
        Long id,
        Long unitId,
        String description,
        BigDecimal amount,
        String currency,
        LocalDate dueDate,
        String status
) {
}