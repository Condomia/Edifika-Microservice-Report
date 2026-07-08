package com.report.demo.shared.domain.model.valueobjects;

import java.math.BigDecimal;

/**
 * Value object que representa un monto monetario con su moneda.
 * Se usará en pagos y deudas del sistema.
 */
public record Money(BigDecimal amount, String currency) {
    public Money {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("El monto no puede ser negativo");
    }
}
