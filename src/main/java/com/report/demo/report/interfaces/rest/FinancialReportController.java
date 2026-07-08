package com.report.demo.report.interfaces.rest;

import com.report.demo.report.domain.model.queries.GetFinancialReportByBuildingIdQuery;
import com.report.demo.report.domain.services.FinancialReportQueryService;
import com.report.demo.report.interfaces.rest.resources.FinancialReportResource;
import com.report.demo.report.interfaces.rest.transform.FinancialReportResourceFromEntityAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/reports", produces = "application/json")
public class FinancialReportController {

    private final FinancialReportQueryService financialReportQueryService;

    public FinancialReportController(FinancialReportQueryService financialReportQueryService) {
        this.financialReportQueryService = financialReportQueryService;
    }

    @GetMapping("/financial/buildings/{buildingId}")
    public ResponseEntity<FinancialReportResource> getFinancialReportByBuildingId(@PathVariable Long buildingId) {
        var query = new GetFinancialReportByBuildingIdQuery(buildingId);
        var financialReport = financialReportQueryService.handle(query);
        var resource = FinancialReportResourceFromEntityAssembler.toResourceFromEntity(financialReport);

        return ResponseEntity.ok(resource);
    }
}