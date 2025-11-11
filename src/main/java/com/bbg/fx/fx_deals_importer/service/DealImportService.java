package com.bbg.fx.fx_deals_importer.service;

import com.bbg.fx.fx_deals_importer.api.dto.DealRequest;
import com.bbg.fx.fx_deals_importer.api.dto.ImportResult;
import com.bbg.fx.fx_deals_importer.model.Deal;
import com.bbg.fx.fx_deals_importer.repository.DealRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Locale;

@Service
public class DealImportService {

    private final DealRepository repo;

    public DealImportService(DealRepository repo) {
        this.repo = repo;
    }

    public ImportResult importDeals(List<DealRequest> requests) {
        AtomicInteger success = new AtomicInteger();
        List<ImportResult.RowError> errors = new ArrayList<>();

        for (int i = 0; i < requests.size(); i++) {
            int row = i + 1;
            DealRequest r = requests.get(i);
            try {
                persistOne(r);
                success.incrementAndGet();
            } catch (DuplicateDealException e) {
                errors.add(new ImportResult.RowError(row, r.getDealUniqueId(), "DUPLICATE", e.getMessage()));
            } catch (Exception e) {
                errors.add(new ImportResult.RowError(row, r.getDealUniqueId(), "ERROR", e.getMessage()));
            }
        }
        return new ImportResult(success.get(), requests.size() - success.get(), errors);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void persistOne(DealRequest r) {
        if (repo.existsByDealUniqueId(r.getDealUniqueId())) {
            throw new DuplicateDealException("Deal with id " + r.getDealUniqueId() + " already exists");
        }

        String from = r.getFromCurrency() != null ? r.getFromCurrency().toUpperCase(Locale.ROOT) : null;
        String to = r.getToCurrency() != null ? r.getToCurrency().toUpperCase(Locale.ROOT) : null;

        Deal d = new Deal();
        d.setDealUniqueId(r.getDealUniqueId());
        d.setFromCurrency(from);
        d.setToCurrency(to);
        OffsetDateTime dealTimestamp = r.getDealTimestamp() != null 
            ? r.getDealTimestamp().atOffset(ZoneOffset.UTC) 
            : null;
        d.setDealTimestamp(dealTimestamp);
        d.setAmount(r.getAmount());
        try {
            repo.saveAndFlush(d);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateDealException("Deal with id " + r.getDealUniqueId() + " already exists");
        }
    }

    public static class DuplicateDealException extends RuntimeException {
        public DuplicateDealException(String msg) { super(msg); }
    }
}