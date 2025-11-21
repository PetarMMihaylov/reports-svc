package app.web.mapper;

import app.model.Report;
import app.web.dto.CreateReportRequest;
import app.web.dto.ReportResponse;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class ReportMapper {

    public static ReportResponse mapFromReportToResponse(Report report) {

        return ReportResponse.builder()
                .id(report.getId())
                .userId(report.getUserId())
                .startDate(report.getStartDate())
                .endDate(report.getEndDate())
                .totalClaims(report.getTotalClaims())
                .totalApprovedClaims(report.getTotalApprovedClaims())
                .totalReimbursedAmount(report.getTotalReimbursedAmount())
                .totalTransactions(report.getTotalTransactions())
                .createdAt(report.getCreatedAt())
                .build();
    }

    public static Report mapFromCreateRequestToReport(CreateReportRequest request) {

        return Report.builder()
                .userId(request.getUserId())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .totalClaims(request.getTotalClaims())
                .totalApprovedClaims(request.getTotalApprovedClaims())
                .totalReimbursedAmount(request.getTotalReimbursedAmount())
                .totalTransactions(request.getTotalTransactions())
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .build();
    }
}
