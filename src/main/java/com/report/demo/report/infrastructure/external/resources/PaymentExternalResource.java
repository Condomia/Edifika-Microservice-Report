package com.report.demo.report.infrastructure.external.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentExternalResource(
        Long id,
        Long debtId,
        Long userId,
        BigDecimal amount,
        String currency,
        LocalDate paymentDate,
        String paymentMethod,
        String status
) {
}