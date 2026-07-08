package com.report.demo.report.domain.model.aggregates;

import com.report.demo.report.domain.model.entities.AreaFinancialReport;
import com.report.demo.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class FinancialReport extends AuditableAbstractAggregateRoot<FinancialReport> {

    @Column(nullable = false)
    private Long buildingId;

    @Column(nullable = false)
    private BigDecimal totalDebt;

    @Column(nullable = false)
    private BigDecimal totalOverdueDebt;

    @Column(nullable = false)
    private BigDecimal totalCollectedFromDebts;

    @Column(nullable = false)
    private BigDecimal collectionRate;

    @Column(nullable = false)
    private BigDecimal overdueRate;

    @Column(nullable = false)
    private BigDecimal totalCollectedFromReservations;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AreaFinancialReport> areas = new ArrayList<>();

    protected FinancialReport() {
    }

    public FinancialReport(
            Long buildingId,
            BigDecimal totalDebt,
            BigDecimal totalOverdueDebt,
            BigDecimal totalCollectedFromDebts,
            BigDecimal collectionRate,
            BigDecimal overdueRate,
            BigDecimal totalCollectedFromReservations,
            List<AreaFinancialReport> areas
    ) {
        this.buildingId = buildingId;
        this.totalDebt = totalDebt;
        this.totalOverdueDebt = totalOverdueDebt;
        this.totalCollectedFromDebts = totalCollectedFromDebts;
        this.collectionRate = collectionRate;
        this.overdueRate = overdueRate;
        this.totalCollectedFromReservations = totalCollectedFromReservations;
        this.areas = areas;
    }
}