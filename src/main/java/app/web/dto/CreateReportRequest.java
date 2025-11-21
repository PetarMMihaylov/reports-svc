package app.web.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class CreateReportRequest {

    private UUID userId;

    private LocalDate startDate;
    private LocalDate endDate;

    private int totalClaims;
    private int totalApprovedClaims;
    private BigDecimal totalReimbursedAmount;
    private int totalTransactions;
}