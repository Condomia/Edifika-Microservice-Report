package com.report.demo.report.application.internal.queryservices;

import com.report.demo.report.domain.model.aggregates.FinancialReport;
import com.report.demo.report.domain.model.entities.AreaFinancialReport;
import com.report.demo.report.domain.model.queries.GetFinancialReportByBuildingIdQuery;
import com.report.demo.report.infrastructure.external.clients.CommonAreaClient;
import com.report.demo.report.infrastructure.external.clients.PaymentClient;
import com.report.demo.report.infrastructure.external.clients.ResidentialClient;
import com.report.demo.report.infrastructure.external.clients.ReservationClient;
import com.report.demo.report.infrastructure.external.resources.CommonAreaExternalResource;
import com.report.demo.report.infrastructure.external.resources.CommonAreaRulesExternalResource;
import com.report.demo.report.infrastructure.external.resources.DebtExternalResource;
import com.report.demo.report.infrastructure.external.resources.PaymentExternalResource;
import com.report.demo.report.infrastructure.external.resources.ReservationExternalResource;
import com.report.demo.report.infrastructure.external.resources.UnitExternalResource;
import com.report.demo.report.infrastructure.external.resources.UserUnitExternalResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinancialReportQueryServiceImplTest {

    @Mock
    private ResidentialClient residentialClient;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private ReservationClient reservationClient;

    @Mock
    private CommonAreaClient commonAreaClient;

    private FinancialReportQueryServiceImpl financialReportQueryService;

    @BeforeEach
    void setUp() {
        financialReportQueryService = new FinancialReportQueryServiceImpl(
                residentialClient,
                paymentClient,
                reservationClient,
                commonAreaClient
        );
    }

    @Test
    void handleShouldReturnFinancialReportWithCalculatedValues() {
        // Arrange
        Long buildingId = 1L;
        var query = new GetFinancialReportByBuildingIdQuery(buildingId);

        var unit = new UnitExternalResource(
                1L,
                2L,
                1L,
                "102",
                1,
                new BigDecimal("78.00"),
                new BigDecimal("84.30"),
                new BigDecimal("4.10"),
                new BigDecimal("4.30"),
                "OCCUPIED"
        );

        var resident = new UserUnitExternalResource(
                1L,
                1L,
                1L,
                2L,
                2L,
                LocalDateTime.of(2026, 1, 10, 8, 0),
                null,
                "ACTIVE"
        );

        var pendingDebt = new DebtExternalResource(
                1L,
                2L,
                "Maintenance fee October",
                new BigDecimal("850.00"),
                "PEN",
                LocalDate.now().minusDays(5),
                "PENDING"
        );

        var paidPayment = new PaymentExternalResource(
                1L,
                1L,
                2L,
                new BigDecimal("500.00"),
                "PEN",
                LocalDate.now().minusDays(2),
                "YAPE",
                "PAID"
        );

        var commonAreaRules = new CommonAreaRulesExternalResource(
                3,
                true,
                new BigDecimal("15.00"),
                true,
                new BigDecimal("30.00"),
                true,
                24,
                new BigDecimal("15.00"),
                true
        );

        var commonArea = new CommonAreaExternalResource(
                3L,
                "Sala de coworking",
                "COWORKING_ROOM",
                "AVAILABLE",
                8,
                "SHARED",
                commonAreaRules
        );

        var activeReservation = new ReservationExternalResource(
                1L,
                2L,
                3L,
                LocalDate.now(),
                18,
                4,
                "ACTIVE",
                "qr-001",
                false
        );

        var cancelledReservationWithPenalty = new ReservationExternalResource(
                2L,
                2L,
                3L,
                LocalDate.now(),
                19,
                4,
                "CANCELLED",
                "qr-002",
                true
        );

        when(residentialClient.getUnitsByBuildingId(buildingId))
                .thenReturn(List.of(unit));

        when(residentialClient.getResidentsByBuildingId(buildingId))
                .thenReturn(List.of(resident));

        when(paymentClient.getDebtsByUnitId(2L))
                .thenReturn(List.of(pendingDebt));

        when(paymentClient.getPaymentsByUserId(2L))
                .thenReturn(List.of(paidPayment));

        when(reservationClient.getAllReservations())
                .thenReturn(List.of(activeReservation, cancelledReservationWithPenalty));

        when(commonAreaClient.getAllCommonAreas())
                .thenReturn(List.of(commonArea));

        // Act
        FinancialReport result = financialReportQueryService.handle(query);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getBuildingId());

        assertEquals(new BigDecimal("850.00"), result.getTotalDebt());
        assertEquals(new BigDecimal("850.00"), result.getTotalOverdueDebt());
        assertEquals(new BigDecimal("500.00"), result.getTotalCollectedFromDebts());

        assertEquals(new BigDecimal("37.04"), result.getCollectionRate());
        assertEquals(new BigDecimal("100.00"), result.getOverdueRate());

        assertEquals(new BigDecimal("30.00"), result.getTotalCollectedFromReservations());

        assertEquals(1, result.getAreas().size());

        AreaFinancialReport areaReport = result.getAreas().get(0);

        assertEquals(3L, areaReport.getAreaId());
        assertEquals("Sala de coworking", areaReport.getAreaName());
        assertEquals(new BigDecimal("30.00"), areaReport.getTotalCollected());
        assertEquals(new BigDecimal("15.00"), areaReport.getTotalPenalties());
        assertEquals(1, areaReport.getTotalReservations());

        verify(residentialClient).getUnitsByBuildingId(buildingId);
        verify(residentialClient).getResidentsByBuildingId(buildingId);
        verify(paymentClient).getDebtsByUnitId(2L);
        verify(paymentClient).getPaymentsByUserId(2L);
        verify(reservationClient).getAllReservations();
        verify(commonAreaClient).getAllCommonAreas();
    }

    @Test
    void handleShouldReturnZeroValuesWhenThereAreNoDebtsPaymentsReservationsOrAreas() {
        // Arrange
        Long buildingId = 1L;
        var query = new GetFinancialReportByBuildingIdQuery(buildingId);

        when(residentialClient.getUnitsByBuildingId(buildingId))
                .thenReturn(List.of());

        when(residentialClient.getResidentsByBuildingId(buildingId))
                .thenReturn(List.of());

        when(reservationClient.getAllReservations())
                .thenReturn(List.of());

        when(commonAreaClient.getAllCommonAreas())
                .thenReturn(List.of());

        // Act
        FinancialReport result = financialReportQueryService.handle(query);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getBuildingId());

        assertEquals(BigDecimal.ZERO, result.getTotalDebt());
        assertEquals(BigDecimal.ZERO, result.getTotalOverdueDebt());
        assertEquals(BigDecimal.ZERO, result.getTotalCollectedFromDebts());
        assertEquals(BigDecimal.ZERO, result.getCollectionRate());
        assertEquals(BigDecimal.ZERO, result.getOverdueRate());
        assertEquals(BigDecimal.ZERO, result.getTotalCollectedFromReservations());
        assertTrue(result.getAreas().isEmpty());

        verify(residentialClient).getUnitsByBuildingId(buildingId);
        verify(residentialClient).getResidentsByBuildingId(buildingId);
        verify(reservationClient).getAllReservations();
        verify(commonAreaClient).getAllCommonAreas();

        verify(paymentClient, never()).getDebtsByUnitId(anyLong());
        verify(paymentClient, never()).getPaymentsByUserId(anyLong());
    }

    @Test
    void handleShouldIgnorePendingPaymentsForCollectedFromDebts() {
        // Arrange
        Long buildingId = 1L;
        var query = new GetFinancialReportByBuildingIdQuery(buildingId);

        var unit = new UnitExternalResource(
                1L,
                2L,
                1L,
                "102",
                1,
                new BigDecimal("78.00"),
                new BigDecimal("84.30"),
                new BigDecimal("4.10"),
                new BigDecimal("4.30"),
                "OCCUPIED"
        );

        var resident = new UserUnitExternalResource(
                1L,
                1L,
                1L,
                2L,
                2L,
                LocalDateTime.now(),
                null,
                "ACTIVE"
        );

        var debt = new DebtExternalResource(
                1L,
                2L,
                "Maintenance fee",
                new BigDecimal("1000.00"),
                "PEN",
                LocalDate.now().plusDays(10),
                "PENDING"
        );

        var pendingPayment = new PaymentExternalResource(
                1L,
                1L,
                2L,
                new BigDecimal("500.00"),
                "PEN",
                LocalDate.now(),
                "CARD",
                "PENDING"
        );

        when(residentialClient.getUnitsByBuildingId(buildingId))
                .thenReturn(List.of(unit));

        when(residentialClient.getResidentsByBuildingId(buildingId))
                .thenReturn(List.of(resident));

        when(paymentClient.getDebtsByUnitId(2L))
                .thenReturn(List.of(debt));

        when(paymentClient.getPaymentsByUserId(2L))
                .thenReturn(List.of(pendingPayment));

        when(reservationClient.getAllReservations())
                .thenReturn(List.of());

        when(commonAreaClient.getAllCommonAreas())
                .thenReturn(List.of());

        // Act
        FinancialReport result = financialReportQueryService.handle(query);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("1000.00"), result.getTotalDebt());
        assertEquals(BigDecimal.ZERO, result.getTotalOverdueDebt());
        assertEquals(BigDecimal.ZERO, result.getTotalCollectedFromDebts());
        assertEquals(new BigDecimal("0.00"), result.getCollectionRate());
        assertEquals(new BigDecimal("0.00"), result.getOverdueRate());
    }
}