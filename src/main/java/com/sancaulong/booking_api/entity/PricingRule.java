package com.sancaulong.booking_api.entity;

import java.math.BigDecimal;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="pricing_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class PricingRule {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_name", nullable= false)
    private String ruleName;

    @Column (name="start_time", nullable=false)
    private LocalTime startTime;

    @Column (name="end_time", nullable=false)
    private LocalTime endTime;

    @Column(name = "price_multiplier", nullable=false, precision=4, scale=2)
    private BigDecimal priceMultiplier;

    @Column(name="is_active")
    private boolean isActive;
}
