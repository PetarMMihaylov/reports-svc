package app;

import app.exception.ReportNotFoundException;
import app.model.Report;
import app.repository.ReportRepository;
import app.service.ReportService;
import app.web.dto.CreateReportRequest;
import app.web.dto.ReportResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
@SpringBootTest
public class CreateReportITest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportRepository reportRepository;

    @Test
    void createAndFetchReport_happyPath() {

        UUID userId = UUID.randomUUID();

        CreateReportRequest request = CreateReportRequest.builder()
                .userId(userId)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .totalClaims(5)
                .totalApprovedClaims(4)
                .totalReimbursedAmount(BigDecimal.valueOf(200.00).setScale(2))
                .totalTransactions(7)
                .build();

        ReportResponse created = reportService.createReport(request);

        Report saved = reportRepository.findByIdAndDeletedFalse(created.getId())
                .orElseThrow(() -> new ReportNotFoundException("Report was not saved"));

        assertEquals(request.getUserId(), saved.getUserId());
        assertEquals(request.getStartDate(), saved.getStartDate());
        assertEquals(request.getEndDate(), saved.getEndDate());
        assertEquals(request.getTotalClaims(), saved.getTotalClaims());
        assertEquals(request.getTotalApprovedClaims(), saved.getTotalApprovedClaims());
        assertEquals(request.getTotalReimbursedAmount(), saved.getTotalReimbursedAmount());
        assertEquals(request.getTotalTransactions(), saved.getTotalTransactions());
        assertNotNull(saved.getCreatedAt());
    }
}

