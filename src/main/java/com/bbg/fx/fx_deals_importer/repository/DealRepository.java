package com.bbg.fx.fx_deals_importer.repository;

import com.bbg.fx.fx_deals_importer.model.Deal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DealRepository extends JpaRepository<Deal, Long> {
    boolean existsByDealUniqueId(String dealUniqueId);
    Optional<Deal> findByDealUniqueId(String dealUniqueId);
}