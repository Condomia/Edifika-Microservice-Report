package com.report.demo.report.interfaces.rest;

import com.report.demo.report.domain.model.aggregates.FinancialReport;
import com.report.demo.report.domain.model.entities.AreaFinancialReport;
import com.report.demo.report.domain.model.queries.GetFinancialReportByBuildingIdQuery;
import com.report.demo.report.domain.services.FinancialReportQueryService;
import com.report.demo.report.interfaces.rest.resources.FinancialReportResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinancialReportControllerTest {

    @Mock
    private FinancialReportQueryService financialReportQueryService;

    private FinancialReportController financialReportController;

    @BeforeEach
    void setUp() {
        financialReportController = new FinancialReportController(financialReportQueryService);
    }

    @Test
    void getFinancialReportByBuildingIdShouldReturnFinancialReportResource() {
        // Arrange
        Long buildingId = 1L;

        var areaFinancialReport = new AreaFinancialReport(
                3L,
                "Sala de coworking",
                new BigDecimal("30.00"),
                new BigDecimal("15.00"),
                1
        );

        var financialReport = new FinancialReport(
                buildingId,
                new BigDecimal("850.00"),
                new BigDecimal("850.00"),
                new BigDecimal("500.00"),
                new BigDecimal("37.04"),
                new BigDecimal("100.00"),
                new BigDecimal("30.00"),
                List.of(areaFinancialReport)
        );

        when(financialReportQueryService.handle(
                argThat(query -> query.buildingId().equals(buildingId))
        )).thenReturn(financialReport);

        // Act
        ResponseEntity<FinancialReportResource> response =
                financialReportController.getFinancialReportByBuildingId(buildingId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        var resource = response.getBody();

        assertEquals(1L, resource.buildingId());
        assertEquals(new BigDecimal("850.00"), resource.totalDebt());
        assertEquals(new BigDecimal("850.00"), resource.totalOverdueDebt());
        assertEquals(new BigDecimal("500.00"), resource.totalCollectedFromDebts());
        assertEquals(new BigDecimal("37.04"), resource.collectionRate());
        assertEquals(new BigDecimal("100.00"), resource.overdueRate());
        assertEquals(new BigDecimal("30.00"), resource.totalCollectedFromReservations());

        assertNotNull(resource.areas());
        assertEquals(1, resource.areas().size());

        var areaResource = resource.areas().get(0);

        assertEquals(3L, areaResource.areaId());
        assertEquals("Sala de coworking", areaResource.areaName());
        assertEquals(new BigDecimal("30.00"), areaResource.totalCollected());
        assertEquals(new BigDecimal("15.00"), areaResource.totalPenalties());
        assertEquals(1, areaResource.totalReservations());

        verify(financialReportQueryService, times(1))
                .handle(argThat(query -> query.buildingId().equals(buildingId)));

        verifyNoMoreInteractions(financialReportQueryService);
    }

    @Test
    void getFinancialReportByBuildingIdShouldReturnEmptyReportWhenThereIsNoFinancialData() {
        // Arrange
        Long buildingId = 2L;

        var financialReport = new FinancialReport(
                buildingId,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                List.of()
        );

        when(financialReportQueryService.handle(
                argThat(query -> query.buildingId().equals(buildingId))
        )).thenReturn(financialReport);

        // Act
        ResponseEntity<FinancialReportResource> response =
                financialReportController.getFinancialReportByBuildingId(buildingId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        var resource = response.getBody();

        assertEquals(2L, resource.buildingId());
        assertEquals(BigDecimal.ZERO, resource.totalDebt());
        assertEquals(BigDecimal.ZERO, resource.totalOverdueDebt());
        assertEquals(BigDecimal.ZERO, resource.totalCollectedFromDebts());
        assertEquals(BigDecimal.ZERO, resource.collectionRate());
        assertEquals(BigDecimal.ZERO, resource.overdueRate());
        assertEquals(BigDecimal.ZERO, resource.totalCollectedFromReservations());

        assertNotNull(resource.areas());
        assertTrue(resource.areas().isEmpty());

        verify(financialReportQueryService, times(1))
                .handle(argThat(query -> query.buildingId().equals(buildingId)));

        verifyNoMoreInteractions(financialReportQueryService);
    }
}