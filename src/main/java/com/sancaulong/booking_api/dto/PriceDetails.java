package com.sancaulong.booking_api.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PriceDetails {
    private BigDecimal totalBasePrice;    // Tổng giá gốc (ví dụ: 2h = 200k)
    private BigDecimal finalTotalPrice;   // Tổng tiền cuối cùng (ví dụ: 220k)
    private BigDecimal averageMultiplier; // Hệ số trung bình (ví dụ: 1.10)
}