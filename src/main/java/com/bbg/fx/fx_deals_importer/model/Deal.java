package com.bbg.fx.fx_deals_importer.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "deals", indexes = {
    @Index(name = "ix_deals_timestamp", columnList = "deal_timestamp")
}, uniqueConstraints = {
    @UniqueConstraint(name = "ux_deals_unique_id", columnNames = "deal_unique_id")
})
public class Deal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "deal_unique_id", nullable = false, length = 128)
    private String dealUniqueId;

    @Column(name = "from_currency", nullable = false, length = 3)
    private String fromCurrency;

    @Column(name = "to_currency", nullable = false, length = 3)
    private String toCurrency;

   
    @Column(name = "deal_timestamp", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime dealTimestamp;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;


    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime createdAt = OffsetDateTime.now(ZoneOffset.UTC);

    public Long getId() { 
        return id;
    }
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
    public OffsetDateTime getDealTimestamp() { 
        return dealTimestamp; 
    }
    public void setDealTimestamp(OffsetDateTime dealTimestamp) { 
        this.dealTimestamp = dealTimestamp; 
    }
    public BigDecimal getAmount() { 
        return amount; 
    }
    public void setAmount(BigDecimal amount) { 
        this.amount = amount; 
    }
    public OffsetDateTime getCreatedAt() { 
        return createdAt; 
    }
    public void setCreatedAt(OffsetDateTime createdAt) { 
        this.createdAt = createdAt; 
    }
}