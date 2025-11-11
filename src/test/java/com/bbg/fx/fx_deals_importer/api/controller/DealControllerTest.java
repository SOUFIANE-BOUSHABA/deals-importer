package com.bbg.fx.fx_deals_importer.api.controller;

import com.bbg.fx.fx_deals_importer.api.dto.DealRequest;
import com.bbg.fx.fx_deals_importer.api.dto.ImportResult;
import com.bbg.fx.fx_deals_importer.service.DealImportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DealController.class)
class DealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DealImportService dealImportService;

    @Autowired
    private ObjectMapper objectMapper;

    private DealRequest validDealRequest;
    private ImportResult successResult;

    @BeforeEach
    void setUp() {
        validDealRequest = new DealRequest();
        validDealRequest.setDealUniqueId("D1001");
        validDealRequest.setFromCurrency("USD");
        validDealRequest.setToCurrency("EUR");
        validDealRequest.setDealTimestamp(Instant.parse("2024-01-01T00:00:00Z"));
        validDealRequest.setAmount(new BigDecimal("1000.00"));

        successResult = new ImportResult(1, 0, List.of());
    }

    @Test
    void testImportJson_Success() throws Exception {
        when(dealImportService.importDeals(anyList())).thenReturn(successResult);

        List<DealRequest> requests = List.of(validDealRequest);

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imported").value(1))
                .andExpect(jsonPath("$.failed").value(0))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void testImportJson_MultipleDeals() throws Exception {
        DealRequest deal2 = new DealRequest();
        deal2.setDealUniqueId("D1002");
        deal2.setFromCurrency("EUR");
        deal2.setToCurrency("GBP");
        deal2.setDealTimestamp(Instant.parse("2024-01-02T00:00:00Z"));
        deal2.setAmount(new BigDecimal("2500.50"));

        ImportResult result = new ImportResult(2, 0, List.of());
        when(dealImportService.importDeals(anyList())).thenReturn(result);

        List<DealRequest> requests = Arrays.asList(validDealRequest, deal2);

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imported").value(2))
                .andExpect(jsonPath("$.failed").value(0));
    }

    @Test
    void testImportJson_ValidationError_MissingDealUniqueId() throws Exception {
        DealRequest invalidDeal = new DealRequest();
        invalidDeal.setFromCurrency("USD");
        invalidDeal.setToCurrency("EUR");
        invalidDeal.setDealTimestamp(Instant.parse("2024-01-01T00:00:00Z"));
        invalidDeal.setAmount(new BigDecimal("1000.00"));

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(invalidDeal))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testImportJson_ValidationError_InvalidCurrency() throws Exception {
        DealRequest invalidDeal = new DealRequest();
        invalidDeal.setDealUniqueId("D1001");
        invalidDeal.setFromCurrency("XXX"); // Invalid ISO code
        invalidDeal.setToCurrency("EUR");
        invalidDeal.setDealTimestamp(Instant.parse("2024-01-01T00:00:00Z"));
        invalidDeal.setAmount(new BigDecimal("1000.00"));

        // Invalid currency will pass JSON validation but fail at service level
        // The service will process it and return it as an error in the result
        ImportResult result = new ImportResult(0, 1, 
            List.of(new ImportResult.RowError(1, "D1001", "ERROR", "Invalid currency code")));
        when(dealImportService.importDeals(anyList())).thenReturn(result);

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(invalidDeal))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imported").value(0))
                .andExpect(jsonPath("$.failed").value(1));
    }

    @Test
    void testImportJson_ValidationError_MissingAmount() throws Exception {
        DealRequest invalidDeal = new DealRequest();
        invalidDeal.setDealUniqueId("D1001");
        invalidDeal.setFromCurrency("USD");
        invalidDeal.setToCurrency("EUR");
        invalidDeal.setDealTimestamp(Instant.parse("2024-01-01T00:00:00Z"));
        // amount is null

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(invalidDeal))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testImportJson_ValidationError_NegativeAmount() throws Exception {
        DealRequest invalidDeal = new DealRequest();
        invalidDeal.setDealUniqueId("D1001");
        invalidDeal.setFromCurrency("USD");
        invalidDeal.setToCurrency("EUR");
        invalidDeal.setDealTimestamp(Instant.parse("2024-01-01T00:00:00Z"));
        invalidDeal.setAmount(new BigDecimal("-100.00")); // Negative amount

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(invalidDeal))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testImportJson_ValidationError_ZeroAmount() throws Exception {
        DealRequest invalidDeal = new DealRequest();
        invalidDeal.setDealUniqueId("D1001");
        invalidDeal.setFromCurrency("USD");
        invalidDeal.setToCurrency("EUR");
        invalidDeal.setDealTimestamp(Instant.parse("2024-01-01T00:00:00Z"));
        invalidDeal.setAmount(BigDecimal.ZERO); // Zero amount

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(invalidDeal))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testImportJson_ValidationError_MissingTimestamp() throws Exception {
        DealRequest invalidDeal = new DealRequest();
        invalidDeal.setDealUniqueId("D1001");
        invalidDeal.setFromCurrency("USD");
        invalidDeal.setToCurrency("EUR");
        // timestamp is null
        invalidDeal.setAmount(new BigDecimal("1000.00"));

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(invalidDeal))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testImportJson_ValidationError_DealUniqueIdTooLong() throws Exception {
        DealRequest invalidDeal = new DealRequest();
        invalidDeal.setDealUniqueId("A".repeat(129)); // Exceeds 128 chars
        invalidDeal.setFromCurrency("USD");
        invalidDeal.setToCurrency("EUR");
        invalidDeal.setDealTimestamp(Instant.parse("2024-01-01T00:00:00Z"));
        invalidDeal.setAmount(new BigDecimal("1000.00"));

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(invalidDeal))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testImportJson_WithDuplicates() throws Exception {
        ImportResult resultWithErrors = new ImportResult(
                1, 1,
                List.of(new ImportResult.RowError(2, "D1001", "DUPLICATE", "Deal already exists"))
        );
        when(dealImportService.importDeals(anyList())).thenReturn(resultWithErrors);

        List<DealRequest> requests = Arrays.asList(validDealRequest, validDealRequest);

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imported").value(1))
                .andExpect(jsonPath("$.failed").value(1))
                .andExpect(jsonPath("$.errors[0].code").value("DUPLICATE"));
    }

    @Test
    void testUploadCsv_Success() throws Exception {
        String csvContent = "dealUniqueId,fromCurrency,toCurrency,dealTimestamp,amount\n" +
                "D1001,USD,EUR,2024-01-01T00:00:00Z,1000.00\n" +
                "D1002,EUR,GBP,2024-01-02T00:00:00Z,2500.50";

        MockMultipartFile file = new MockMultipartFile(
                "file", "deals.csv", "text/csv", csvContent.getBytes());

        ImportResult result = new ImportResult(2, 0, List.of());
        when(dealImportService.importDeals(anyList())).thenReturn(result);

        mockMvc.perform(multipart("/api/deals/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imported").value(2))
                .andExpect(jsonPath("$.failed").value(0));
    }

    @Test
    void testUploadCsv_WithDuplicates() throws Exception {
        String csvContent = "dealUniqueId,fromCurrency,toCurrency,dealTimestamp,amount\n" +
                "D1001,USD,EUR,2024-01-01T00:00:00Z,1000.00\n" +
                "D1001,USD,EUR,2024-01-02T00:00:00Z,2000.00";

        MockMultipartFile file = new MockMultipartFile(
                "file", "deals.csv", "text/csv", csvContent.getBytes());

        ImportResult result = new ImportResult(
                1, 1,
                List.of(new ImportResult.RowError(2, "D1001", "DUPLICATE", "Deal already exists"))
        );
        when(dealImportService.importDeals(anyList())).thenReturn(result);

        mockMvc.perform(multipart("/api/deals/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imported").value(1))
                .andExpect(jsonPath("$.failed").value(1))
                .andExpect(jsonPath("$.errors[0].code").value("DUPLICATE"));
    }

    @Test
    void testUploadCsv_InvalidTimestamp() throws Exception {
        String csvContent = "dealUniqueId,fromCurrency,toCurrency,dealTimestamp,amount\n" +
                "D1001,USD,EUR,invalid-date,1000.00";

        MockMultipartFile file = new MockMultipartFile(
                "file", "deals.csv", "text/csv", csvContent.getBytes());

        mockMvc.perform(multipart("/api/deals/upload")
                        .file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUploadCsv_InvalidAmount() throws Exception {
        String csvContent = "dealUniqueId,fromCurrency,toCurrency,dealTimestamp,amount\n" +
                "D1001,USD,EUR,2024-01-01T00:00:00Z,not-a-number";

        MockMultipartFile file = new MockMultipartFile(
                "file", "deals.csv", "text/csv", csvContent.getBytes());

        mockMvc.perform(multipart("/api/deals/upload")
                        .file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUploadCsv_EmptyFile() throws Exception {
        String csvContent = "dealUniqueId,fromCurrency,toCurrency,dealTimestamp,amount\n";
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.csv", "text/csv", csvContent.getBytes());

        ImportResult result = new ImportResult(0, 0, List.of());
        when(dealImportService.importDeals(anyList())).thenReturn(result);

        mockMvc.perform(multipart("/api/deals/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imported").value(0))
                .andExpect(jsonPath("$.failed").value(0));
    }

    @Test
    void testUploadCsv_LowercaseCurrency() throws Exception {
        String csvContent = "dealUniqueId,fromCurrency,toCurrency,dealTimestamp,amount\n" +
                "D1001,usd,eur,2024-01-01T00:00:00Z,1000.00";

        MockMultipartFile file = new MockMultipartFile(
                "file", "deals.csv", "text/csv", csvContent.getBytes());

        ImportResult result = new ImportResult(1, 0, List.of());
        when(dealImportService.importDeals(anyList())).thenReturn(result);

        mockMvc.perform(multipart("/api/deals/upload")
                        .file(file))
                .andExpect(status().isOk());
    }
}

