package com.bbg.fx.fx_deals_importer.api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;

import com.bbg.fx.fx_deals_importer.common.validation.Iso4217;

public class DealRequest {
    @NotBlank
    @Size(max = 128)
    private String dealUniqueId;

    @NotBlank
    @Iso4217
    private String fromCurrency;

    @NotBlank
    @Iso4217
    private String toCurrency;

    @NotNull
    private Instant dealTimestamp;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = false)
    private BigDecimal amount;

    public String getDealUniqueId() {
         return dealUniqueId; 
    }
    public void setDealUniqueId(String dealUniqueId) { 
        this.dealUniqueId = dealUniqueId; 
    }
    public String getFromCurrency() { 
        return fromCurrency; 
    }
    public void setFromCurrency(String fromCurrency) { 
        this.fromCurrency = fromCurrency; 
    }
    public String getToCurrency() { 
        return toCurrency; 
    }
    public void setToCurrency(String toCurrency) { 
        this.toCurrency = toCurrency; 
    }
    public Instant getDealTimestamp() { 
        return dealTimestamp; 
    }
    public void setDealTimestamp(Instant dealTimestamp) { 
        this.dealTimestamp = dealTimestamp; 
    }
    public BigDecimal getAmount() { 
        return amount; 
    }
    public void setAmount(BigDecimal amount) { 
        this.amount = amount; 
    }
}