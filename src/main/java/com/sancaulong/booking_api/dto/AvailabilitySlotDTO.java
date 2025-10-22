package com.sancaulong.booking_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AvailabilitySlotDTO {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal price; // Giá đã tính cho khung giờ này
    private boolean isAvailable; // true = trống, false = đã bị đặt
}