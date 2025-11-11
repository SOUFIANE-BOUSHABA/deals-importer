package com.bbg.fx.fx_deals_importer.api.controller;

import com.bbg.fx.fx_deals_importer.api.dto.DealRequest;
import com.bbg.fx.fx_deals_importer.api.dto.ImportResult;
import com.bbg.fx.fx_deals_importer.service.DealImportService;
import jakarta.validation.Valid;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.format.DateTimeParseException;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
  import java.util.List;

@RestController
@RequestMapping("/api/deals")
@Validated
public class DealController {

    private final DealImportService service;

    public DealController(DealImportService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ImportResult importJson(@RequestBody @Valid List<DealRequest> body) {
        return service.importDeals(body);
    }

    @PostMapping(
        path = "/upload",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ImportResult uploadCsv(@RequestPart("file") MultipartFile file) {
        try (var reader = new InputStreamReader(file.getInputStream())) {
            CSVParser parser = CSVFormat.DEFAULT
                .withHeader("dealUniqueId","fromCurrency","toCurrency","dealTimestamp","amount")
                .withSkipHeaderRecord(true)
                .parse(reader);
            List<DealRequest> batch = new ArrayList<>();
            for (CSVRecord rec : parser) {
                try {
                    DealRequest r = new DealRequest();
                    r.setDealUniqueId(rec.get("dealUniqueId"));
                    String fromCurrency = rec.get("fromCurrency");
                    String toCurrency = rec.get("toCurrency");
                    r.setFromCurrency(fromCurrency != null ? fromCurrency.toUpperCase() : null);
                    r.setToCurrency(toCurrency != null ? toCurrency.toUpperCase() : null);
                    r.setDealTimestamp(Instant.parse(rec.get("dealTimestamp")));
                    r.setAmount(new BigDecimal(rec.get("amount")));
                    batch.add(r);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid row: " + e.getMessage(), e);
                }
            }
            return service.importDeals(batch);
        } catch (DateTimeParseException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to process CSV file", e);
        }
    }
}