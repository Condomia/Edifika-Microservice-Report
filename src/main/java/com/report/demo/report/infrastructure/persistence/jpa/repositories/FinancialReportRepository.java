package com.report.demo.report.infrastructure.persistence.jpa.repositories;

import com.report.demo.report.domain.model.aggregates.FinancialReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinancialReportRepository extends JpaRepository<FinancialReport, Long> {

    List<FinancialReport> findByBuildingId(Long buildingId);
}