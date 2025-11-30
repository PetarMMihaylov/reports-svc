package app.web;

import app.exception.ReportNotFoundException;
import app.service.ReportService;
import app.web.dto.CreateReportRequest;
import app.web.dto.ReportResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportController.class)
class ReportControllerAPITest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getReports_whenUserIdProvided_thenReturnFullReportList() throws Exception {

        UUID userId = UUID.randomUUID();
        ReportResponse response = ReportResponse.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .totalClaims(12)
                .totalApprovedClaims(10)
                .totalReimbursedAmount(BigDecimal.valueOf(500))
                .totalTransactions(15)
                .createdAt(LocalDateTime.of(2024, 2, 1, 10, 0))
                .build();

        when(reportService.getReportsByUser(userId)).thenReturn(List.of(response));

        MockHttpServletRequestBuilder request = get("/api/v1/reports")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(response.getId().toString()))
                .andExpect(jsonPath("$[0].userId").value(userId.toString()))
                .andExpect(jsonPath("$[0].startDate").value("2024-01-01"))
                .andExpect(jsonPath("$[0].endDate").value("2024-01-31"))
                .andExpect(jsonPath("$[0].totalClaims").value(12))
                .andExpect(jsonPath("$[0].totalApprovedClaims").value(10))
                .andExpect(jsonPath("$[0].totalReimbursedAmount").value(500))
                .andExpect(jsonPath("$[0].totalTransactions").value(15))
                .andExpect(jsonPath("$[0].createdAt").value("2024-02-01T10:00:00"));

        verify(reportService).getReportsByUser(userId);
    }

    @Test
    void getReports_whenMultipleReports_thenReturnFullListWithAllFields() throws Exception {

        UUID userId = UUID.randomUUID();

        ReportResponse report1 = ReportResponse.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .totalClaims(5)
                .totalApprovedClaims(4)
                .totalReimbursedAmount(BigDecimal.valueOf(200))
                .totalTransactions(7)
                .createdAt(LocalDateTime.of(2024, 2, 1, 10, 0))
                .build();

        ReportResponse report2 = ReportResponse.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .startDate(LocalDate.of(2024, 2, 1))
                .endDate(LocalDate.of(2024, 2, 28))
                .totalClaims(8)
                .totalApprovedClaims(7)
                .totalReimbursedAmount(BigDecimal.valueOf(350))
                .totalTransactions(10)
                .createdAt(LocalDateTime.of(2024, 3, 1, 10, 0))
                .build();

        when(reportService.getReportsByUser(userId)).thenReturn(List.of(report1, report2));

        MockHttpServletRequestBuilder request = get("/api/v1/reports")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(report1.getId().toString()))
                .andExpect(jsonPath("$[0].userId").value(report1.getUserId().toString()))
                .andExpect(jsonPath("$[0].startDate").value(report1.getStartDate().toString()))
                .andExpect(jsonPath("$[0].endDate").value(report1.getEndDate().toString()))
                .andExpect(jsonPath("$[0].totalClaims").value(report1.getTotalClaims()))
                .andExpect(jsonPath("$[0].totalApprovedClaims").value(report1.getTotalApprovedClaims()))
                .andExpect(jsonPath("$[0].totalReimbursedAmount").value(report1.getTotalReimbursedAmount().toPlainString()))
                .andExpect(jsonPath("$[0].totalTransactions").value(report1.getTotalTransactions()))
                .andExpect(jsonPath("$[0].createdAt").value("2024-02-01T10:00:00"))
                .andExpect(jsonPath("$[1].id").value(report2.getId().toString()))
                .andExpect(jsonPath("$[1].userId").value(report2.getUserId().toString()))
                .andExpect(jsonPath("$[1].startDate").value(report2.getStartDate().toString()))
                .andExpect(jsonPath("$[1].endDate").value(report2.getEndDate().toString()))
                .andExpect(jsonPath("$[1].totalClaims").value(report2.getTotalClaims()))
                .andExpect(jsonPath("$[1].totalApprovedClaims").value(report2.getTotalApprovedClaims()))
                .andExpect(jsonPath("$[1].totalReimbursedAmount").value(report2.getTotalReimbursedAmount().toPlainString()))
                .andExpect(jsonPath("$[1].totalTransactions").value(report2.getTotalTransactions()))
                .andExpect(jsonPath("$[1].createdAt").value("2024-03-01T10:00:00"));

        verify(reportService).getReportsByUser(userId);
    }

    @Test
    void getReports_whenNoReports_thenReturnEmptyList() throws Exception {

        UUID userId = UUID.randomUUID();
        when(reportService.getReportsByUser(userId)).thenReturn(List.of());

        MockHttpServletRequestBuilder request = get("/api/v1/reports")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(reportService).getReportsByUser(userId);
    }

    @Test
    void getReports_whenUserIdInvalid_thenReturn400() throws Exception {

        MockHttpServletRequestBuilder request = get("/api/v1/reports")
                .param("userId", "not-a-uuid")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());

        verifyNoInteractions(reportService);
    }

    @Test
    void getReportDetails_whenIdExists_thenReturnFullReport() throws Exception {

        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ReportResponse response = ReportResponse.builder()
                .id(id)
                .userId(userId)
                .startDate(LocalDate.of(2024, 3, 1))
                .endDate(LocalDate.of(2024, 3, 31))
                .totalClaims(8)
                .totalApprovedClaims(6)
                .totalReimbursedAmount(BigDecimal.valueOf(300))
                .totalTransactions(9)
                .createdAt(LocalDateTime.of(2024, 4, 1, 12, 0))
                .build();

        when(reportService.getReportById(id)).thenReturn(response);

        MockHttpServletRequestBuilder request = get("/api/v1/reports/{id}", id)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.startDate").value("2024-03-01"))
                .andExpect(jsonPath("$.endDate").value("2024-03-31"))
                .andExpect(jsonPath("$.totalClaims").value(8))
                .andExpect(jsonPath("$.totalApprovedClaims").value(6))
                .andExpect(jsonPath("$.totalReimbursedAmount").value(300))
                .andExpect(jsonPath("$.totalTransactions").value(9))
                .andExpect(jsonPath("$.createdAt").value("2024-04-01T12:00:00"));

        verify(reportService).getReportById(id);
    }

    @Test
    void getReportDetails_whenIdNotFound_thenReturn404() throws Exception {

        UUID id = UUID.randomUUID();
        when(reportService.getReportById(id))
                .thenThrow(new ReportNotFoundException("Report not found"));

        MockHttpServletRequestBuilder request = get("/api/v1/reports/{id}", id)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Report not found"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(reportService).getReportById(id);
    }

    @Test
    void getReportDetails_whenPathVariableInvalid_thenReturn400() throws Exception {

        MockHttpServletRequestBuilder request = get("/api/v1/reports/{id}", "not-a-uuid")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());

        verifyNoInteractions(reportService);
    }

    @Test
    void createReport_whenValidRequest_thenReturnCreatedFullReport() throws Exception {

        CreateReportRequest requestBody = CreateReportRequest.builder()
                .userId(UUID.randomUUID())
                .startDate(LocalDate.of(2024, 5, 1))
                .endDate(LocalDate.of(2024, 5, 31))
                .totalClaims(4)
                .totalApprovedClaims(3)
                .totalReimbursedAmount(BigDecimal.valueOf(150))
                .totalTransactions(6)
                .build();

        ReportResponse created = ReportResponse.builder()
                .id(UUID.randomUUID())
                .userId(requestBody.getUserId())
                .startDate(requestBody.getStartDate())
                .endDate(requestBody.getEndDate())
                .totalClaims(requestBody.getTotalClaims())
                .totalApprovedClaims(requestBody.getTotalApprovedClaims())
                .totalReimbursedAmount(requestBody.getTotalReimbursedAmount())
                .totalTransactions(requestBody.getTotalTransactions())
                .createdAt(LocalDateTime.of(2024, 6, 1, 14, 0))
                .build();

        when(reportService.createReport(any(CreateReportRequest.class))).thenReturn(created);

        MockHttpServletRequestBuilder request = post("/api/v1/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody));

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(created.getId().toString()))
                .andExpect(jsonPath("$.userId").value(created.getUserId().toString()))
                .andExpect(jsonPath("$.startDate").value("2024-05-01"))
                .andExpect(jsonPath("$.endDate").value("2024-05-31"))
                .andExpect(jsonPath("$.totalClaims").value(4))
                .andExpect(jsonPath("$.totalApprovedClaims").value(3))
                .andExpect(jsonPath("$.totalReimbursedAmount").value(150))
                .andExpect(jsonPath("$.totalTransactions").value(6))
                .andExpect(jsonPath("$.createdAt").value("2024-06-01T14:00:00"));

        verify(reportService).createReport(any(CreateReportRequest.class));
    }

    @Test
    void deleteReport_whenIdExists_thenReturnNoContent() throws Exception {

        UUID id = UUID.randomUUID();
        doNothing().when(reportService).deleteReport(id);

        MockHttpServletRequestBuilder request = delete("/api/v1/reports/{id}", id)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        verify(reportService).deleteReport(id);
    }

    @Test
    void deleteReport_whenIdNotFound_thenReturn404() throws Exception {

        UUID id = UUID.randomUUID();
        doThrow(new ReportNotFoundException("Report not found"))
                .when(reportService).deleteReport(id);

        MockHttpServletRequestBuilder request = delete("/api/v1/reports/{id}", id)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Report not found"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(reportService).deleteReport(id);
    }
}
