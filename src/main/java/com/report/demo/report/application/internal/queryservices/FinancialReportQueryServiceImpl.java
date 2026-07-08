package com.report.demo.report.application.internal.queryservices;

import com.report.demo.report.domain.model.aggregates.FinancialReport;
import com.report.demo.report.domain.model.entities.AreaFinancialReport;
import com.report.demo.report.domain.model.queries.GetFinancialReportByBuildingIdQuery;
import com.report.demo.report.domain.services.FinancialReportQueryService;
import com.report.demo.report.infrastructure.external.clients.CommonAreaClient;
import com.report.demo.report.infrastructure.external.clients.PaymentClient;
import com.report.demo.report.infrastructure.external.clients.ResidentialClient;
import com.report.demo.report.infrastructure.external.clients.ReservationClient;
import com.report.demo.report.infrastructure.external.resources.CommonAreaExternalResource;
import com.report.demo.report.infrastructure.external.resources.DebtExternalResource;
import com.report.demo.report.infrastructure.external.resources.PaymentExternalResource;
import com.report.demo.report.infrastructure.external.resources.ReservationExternalResource;
import com.report.demo.report.infrastructure.external.resources.UnitExternalResource;
import com.report.demo.report.infrastructure.external.resources.UserUnitExternalResource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class FinancialReportQueryServiceImpl implements FinancialReportQueryService {

    private final ResidentialClient residentialClient;
    private final PaymentClient paymentClient;
    private final ReservationClient reservationClient;
    private final CommonAreaClient commonAreaClient;

    public FinancialReportQueryServiceImpl(
            ResidentialClient residentialClient,
            PaymentClient paymentClient,
            ReservationClient reservationClient,
            CommonAreaClient commonAreaClient
    ) {
        this.residentialClient = residentialClient;
        this.paymentClient = paymentClient;
        this.reservationClient = reservationClient;
        this.commonAreaClient = commonAreaClient;
    }

    @Override
    public FinancialReport handle(GetFinancialReportByBuildingIdQuery query) {
        Long buildingId = query.buildingId();

        List<UnitExternalResource> units = residentialClient.getUnitsByBuildingId(buildingId);
        List<UserUnitExternalResource> residents = residentialClient.getResidentsByBuildingId(buildingId);

        List<DebtExternalResource> debts = units.stream()
                .flatMap(unit -> paymentClient.getDebtsByUnitId(unit.idUnit()).stream())
                .toList();

        List<PaymentExternalResource> payments = residents.stream()
                .map(UserUnitExternalResource::idUser)
                .distinct()
                .flatMap(userId -> paymentClient.getPaymentsByUserId(userId).stream())
                .toList();

        List<ReservationExternalResource> reservations = reservationClient.getAllReservations();
        List<CommonAreaExternalResource> commonAreas = commonAreaClient.getAllCommonAreas();

        BigDecimal totalDebt = debts.stream()
                .filter(debt -> equalsIgnoreCase(debt.status(), "PENDING"))
                .map(DebtExternalResource::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOverdueDebt = debts.stream()
                .filter(debt -> equalsIgnoreCase(debt.status(), "PENDING"))
                .filter(debt -> debt.dueDate().isBefore(LocalDate.now()))
                .map(DebtExternalResource::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Long> debtIds = debts.stream()
                .map(DebtExternalResource::id)
                .toList();

        BigDecimal totalCollectedFromDebts = payments.stream()
                .filter(payment -> equalsIgnoreCase(payment.status(), "PAID"))
                .filter(payment -> debtIds.contains(payment.debtId()))
                .map(PaymentExternalResource::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal collectionRate = calculatePercentage(
                totalCollectedFromDebts,
                totalCollectedFromDebts.add(totalDebt)
        );

        BigDecimal overdueRate = calculatePercentage(
                totalOverdueDebt,
                totalDebt
        );

        List<AreaFinancialReport> areas = commonAreas.stream()
                .map(area -> buildAreaFinancialReport(area, reservations))
                .toList();

        BigDecimal totalCollectedFromReservations = areas.stream()
                .map(AreaFinancialReport::getTotalCollected)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new FinancialReport(
                buildingId,
                totalDebt,
                totalOverdueDebt,
                totalCollectedFromDebts,
                collectionRate,
                overdueRate,
                totalCollectedFromReservations,
                areas
        );
    }

    private AreaFinancialReport buildAreaFinancialReport(
            CommonAreaExternalResource area,
            List<ReservationExternalResource> reservations
    ) {
        List<ReservationExternalResource> areaReservations = reservations.stream()
                .filter(reservation -> reservation.commonAreaId().equals(area.id()))
                .toList();

        List<ReservationExternalResource> validReservations = areaReservations.stream()
                .filter(reservation -> isValidReservationForIncome(reservation.status()))
                .toList();

        BigDecimal reservationIncome = validReservations.stream()
                .map(reservation -> area.rules().price())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal penaltyIncome = areaReservations.stream()
                .filter(reservation -> Boolean.TRUE.equals(reservation.penaltyApplied()))
                .map(reservation -> area.rules().penaltyAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AreaFinancialReport(
                area.id(),
                area.name(),
                reservationIncome.add(penaltyIncome),
                penaltyIncome,
                validReservations.size()
        );
    }

    private boolean isValidReservationForIncome(String status) {
        return equalsIgnoreCase(status, "ACTIVE") || equalsIgnoreCase(status, "COMPLETED");
    }

    private BigDecimal calculatePercentage(BigDecimal value, BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return value
                .divide(total, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private boolean equalsIgnoreCase(String value, String expected) {
        return value != null && value.equalsIgnoreCase(expected);
    }
}