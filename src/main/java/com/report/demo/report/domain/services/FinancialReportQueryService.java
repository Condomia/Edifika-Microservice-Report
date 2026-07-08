package com.report.demo.report.domain.services;

import com.report.demo.report.domain.model.aggregates.FinancialReport;
import com.report.demo.report.domain.model.queries.GetFinancialReportByBuildingIdQuery;

public interface FinancialReportQueryService {
    FinancialReport handle(GetFinancialReportByBuildingIdQuery query);
}