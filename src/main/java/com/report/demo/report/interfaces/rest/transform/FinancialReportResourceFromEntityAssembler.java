package com.report.demo.report.interfaces.rest.transform;

import com.report.demo.report.domain.model.aggregates.FinancialReport;
import com.report.demo.report.interfaces.rest.resources.FinancialReportResource;

public class FinancialReportResourceFromEntityAssembler {

    public static FinancialReportResource toResourceFromEntity(FinancialReport entity) {
        var areaResources = entity.getAreas()
                .stream()
                .map(AreaFinancialReportResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return new FinancialReportResource(
                entity.getBuildingId(),
                entity.getTotalDebt(),
                entity.getTotalOverdueDebt(),
                entity.getTotalCollectedFromDebts(),
                entity.getCollectionRate(),
                entity.getOverdueRate(),
                entity.getTotalCollectedFromReservations(),
                areaResources
        );
    }
}