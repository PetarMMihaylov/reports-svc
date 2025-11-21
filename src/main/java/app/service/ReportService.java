package app.service;

import app.exception.ReportNotFoundException;
import app.model.Report;
import app.repository.ReportRepository;
import app.web.dto.CreateReportRequest;
import app.web.dto.ReportResponse;
import app.web.mapper.ReportMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public List<ReportResponse> getReportsByUser(UUID userId) {
        return reportRepository.findByUserIdAndDeletedFalse(userId)
                .stream()
                .map(ReportMapper::mapFromReportToResponse)
                .toList();
    }

    public ReportResponse getReportById(UUID id) {
        Report report = reportRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ReportNotFoundException("Report not found"));

        return ReportMapper.mapFromReportToResponse(report);
    }

    public ReportResponse createReport(CreateReportRequest request) {
        Report report = ReportMapper.mapFromCreateRequestToReport(request);
        reportRepository.save(report);
        return ReportMapper.mapFromReportToResponse(report);
    }
}