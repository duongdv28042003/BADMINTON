package com.sancaulong.booking_api.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticatedBookingRequest {
    private Long courtId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}