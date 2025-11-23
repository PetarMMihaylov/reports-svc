package app.service;

import app.exception.ReportNotFoundException;
import app.model.Report;
import app.repository.ReportRepository;
import app.web.dto.CreateReportRequest;
import app.web.dto.ReportResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportUTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportService reportService;

    @Test
    void getReportsByUser_whenThereAre2Reports_thenReturnExactly2MappedResponses() {

        UUID userId = UUID.randomUUID();

        Report report1 = Report.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .totalClaims(10)
                .totalApprovedClaims(8)
                .totalReimbursedAmount(BigDecimal.valueOf(1500))
                .totalTransactions(12)
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .build();

        Report report2 = Report.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .startDate(LocalDate.of(2024, 3, 1))
                .endDate(LocalDate.of(2024, 3, 30))
                .totalClaims(5)
                .totalApprovedClaims(4)
                .totalReimbursedAmount(BigDecimal.valueOf(900))
                .totalTransactions(7)
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .build();

        List<Report> reports = new ArrayList<>();
        reports.add(report1);
        reports.add(report2);

        when(reportRepository.findByUserIdAndDeletedFalse(userId)).thenReturn(reports);

        List<ReportResponse> responses = reportService.getReportsByUser(userId);

        assertEquals(2, responses.size());
        assertEquals(report1.getId(), responses.get(0).getId());
        assertEquals(report2.getId(), responses.get(1).getId());

        verify(reportRepository, times(1)).findByUserIdAndDeletedFalse(userId);
    }

    @Test
    void getReportsByUser_whenNoReportsFound_thenReturnEmptyList() {

        UUID userId = UUID.randomUUID();
        when(reportRepository.findByUserIdAndDeletedFalse(userId)).thenReturn(List.of());

        List<ReportResponse> responses = reportService.getReportsByUser(userId);

        assertEquals(0, responses.size());
        verify(reportRepository, times(1)).findByUserIdAndDeletedFalse(userId);
    }

    @Test
    void getReportById_whenReportExists_thenReturnsMappedResponse() {

        UUID reportId = UUID.randomUUID();

        Report report = Report.builder()
                .id(reportId)
                .userId(UUID.randomUUID())
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .totalClaims(10)
                .totalApprovedClaims(8)
                .totalReimbursedAmount(BigDecimal.valueOf(1500))
                .totalTransactions(12)
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .build();

        when(reportRepository.findByIdAndDeletedFalse(reportId)).thenReturn(Optional.of(report));

        ReportResponse response = reportService.getReportById(reportId);

        assertNotNull(response);
        assertEquals(reportId, response.getId());
        verify(reportRepository, times(1)).findByIdAndDeletedFalse(reportId);
    }

    @Test
    void getReportById_whenReportDoesNotExist_thenThrowsException() {

        UUID reportId = UUID.randomUUID();
        when(reportRepository.findByIdAndDeletedFalse(reportId)).thenReturn(Optional.empty());

        assertThrows(ReportNotFoundException.class, () -> reportService.getReportById(reportId));
        verify(reportRepository, times(1)).findByIdAndDeletedFalse(reportId);
    }

    @Test
    void createReport_whenRequestIsValid_thenSavesReportAndReturnsMappedResponse() {

        CreateReportRequest request = CreateReportRequest.builder()
                .userId(java.util.UUID.randomUUID())
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .totalClaims(10)
                .totalApprovedClaims(8)
                .totalReimbursedAmount(BigDecimal.valueOf(1500))
                .totalTransactions(12)
                .build();

        ArgumentCaptor<Report> captor = ArgumentCaptor.forClass(Report.class);

        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReportResponse response = reportService.createReport(request);

        verify(reportRepository, times(1)).save(captor.capture());
        Report savedReport = captor.getValue();

        assertEquals(request.getUserId(), savedReport.getUserId());
        assertEquals(request.getStartDate(), savedReport.getStartDate());
        assertEquals(request.getEndDate(), savedReport.getEndDate());
        assertEquals(request.getTotalClaims(), savedReport.getTotalClaims());
        assertEquals(request.getTotalApprovedClaims(), savedReport.getTotalApprovedClaims());
        assertEquals(request.getTotalReimbursedAmount(), savedReport.getTotalReimbursedAmount());
        assertEquals(request.getTotalTransactions(), savedReport.getTotalTransactions());

        assertNotNull(savedReport.getCreatedAt());

        assertNotNull(response);
        assertEquals(savedReport.getId(), response.getId());
        assertEquals(savedReport.getUserId(), response.getUserId());
    }

    @Test
    void deleteReport_whenReportExists_thenSetsDeletedTrueAndSaves() {

        UUID reportId = UUID.randomUUID();

        Report report = Report.builder()
                .id(reportId)
                .userId(UUID.randomUUID())
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .totalClaims(10)
                .totalApprovedClaims(8)
                .totalReimbursedAmount(BigDecimal.valueOf(1500))
                .totalTransactions(12)
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .build();

        when(reportRepository.findByIdAndDeletedFalse(reportId)).thenReturn(Optional.of(report));
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Report> captor = ArgumentCaptor.forClass(Report.class);

        reportService.deleteReport(reportId);

        verify(reportRepository, times(1)).findByIdAndDeletedFalse(reportId);
        verify(reportRepository, times(1)).save(captor.capture());

        Report savedReport = captor.getValue();
        assertTrue(savedReport.isDeleted(), "Report should be marked as deleted");
        assertEquals(reportId, savedReport.getId(), "Report ID should not change");
    }

    @Test
    void deleteReport_whenReportDoesNotExist_thenThrowsException() {

        UUID reportId = UUID.randomUUID();
        when(reportRepository.findByIdAndDeletedFalse(reportId)).thenReturn(Optional.empty());

        assertThrows(ReportNotFoundException.class, () -> reportService.deleteReport(reportId));

        verify(reportRepository, times(1)).findByIdAndDeletedFalse(reportId);
        verify(reportRepository, never()).save(any(Report.class));
    }
}
