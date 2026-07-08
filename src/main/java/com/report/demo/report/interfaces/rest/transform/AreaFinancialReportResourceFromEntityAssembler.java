package com.report.demo.report.interfaces.rest.transform;

import com.report.demo.report.domain.model.entities.AreaFinancialReport;
import com.report.demo.report.interfaces.rest.resources.AreaFinancialReportResource;

public class AreaFinancialReportResourceFromEntityAssembler {

    public static AreaFinancialReportResource toResourceFromEntity(AreaFinancialReport entity) {
        return new AreaFinancialReportResource(
                entity.getAreaId(),
                entity.getAreaName(),
                entity.getTotalCollected(),
                entity.getTotalPenalties(),
                entity.getTotalReservations()
        );
    }
}