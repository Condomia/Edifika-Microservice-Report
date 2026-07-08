package com.report.demo.report.domain.model.entities;

import com.report.demo.shared.domain.model.entity.AuditableModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Entity
public class AreaFinancialReport extends AuditableModel {

    @Column(nullable = false)
    private Long areaId;

    @Column(nullable = false)
    private String areaName;

    @Column(nullable = false)
    private BigDecimal totalCollected;

    @Column(nullable = false)
    private BigDecimal totalPenalties;

    @Column(nullable = false)
    private Integer totalReservations;

    protected AreaFinancialReport() {
    }

    public AreaFinancialReport(
            Long areaId,
            String areaName,
            BigDecimal totalCollected,
            BigDecimal totalPenalties,
            Integer totalReservations
    ) {
        this.areaId = areaId;
        this.areaName = areaName;
        this.totalCollected = totalCollected;
        this.totalPenalties = totalPenalties;
        this.totalReservations = totalReservations;
    }
}