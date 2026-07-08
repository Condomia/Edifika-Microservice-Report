package com.report.demo.report.domain.model.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record ReportMoney(
        BigDecimal amount
) {
    public ReportMoney {
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }

        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public ReportMoney add(ReportMoney other) {
        return new ReportMoney(this.amount.add(other.amount));
    }

    public ReportMoney subtract(ReportMoney other) {
        return new ReportMoney(this.amount.subtract(other.amount));
    }
}