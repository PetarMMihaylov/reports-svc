package app.web;

import app.service.ReportService;
import app.web.dto.CreateReportRequest;
import app.web.dto.ReportResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public ResponseEntity<List<ReportResponse>> getReports(@RequestParam("userId") UUID userId) {

        List<ReportResponse> responses = reportService.getReportsByUser(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> getReportDetailsById(@PathVariable UUID id) {

        ReportResponse report = reportService.getReportById(id);
        return ResponseEntity.ok(report);
    }

    @PostMapping
    public ResponseEntity<ReportResponse> createReport(@RequestBody CreateReportRequest request) {
        ReportResponse created = reportService.createReport(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
