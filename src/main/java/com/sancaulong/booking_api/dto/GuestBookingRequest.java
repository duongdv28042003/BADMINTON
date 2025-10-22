package com.sancaulong.booking_api.dto;

// thong tin frontend gui len khi khach dat san

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestBookingRequest {
    private Long courtId;
    private LocalDateTime startTime;
    private LocalDateTime endTime; 
    private String guestName;
    private String guestPhone;
    private String guestEmail; 
}