package com.sancaulong.booking_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sancaulong.booking_api.entity.PricingRule;

@Repository

public interface PricingRuleRepository extends JpaRepository<PricingRule, Long>{
    // select * from pricing_rules where is_active = ?
    List<PricingRule> findByIsActive(boolean isActive);
}
