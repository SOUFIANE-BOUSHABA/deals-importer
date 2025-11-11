package com.bbg.fx.fx_deals_importer.api.dto;

import java.util.List;

public record ImportResult(int imported, int failed, List<RowError> errors) {
    public record RowError(int rowNumber, String dealUniqueId, String code, String message) {}
}