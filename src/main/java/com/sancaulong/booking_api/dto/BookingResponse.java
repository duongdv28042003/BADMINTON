package com.sancaulong.booking_api.dto;

//backend tra ve khi dat san thanh cong

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sancaulong.booking_api.entity.BookingStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder // Dùng Builder Pattern để tạo đối tượng này dễ dàng
public class BookingResponse {
    private Long bookingId;
    private Long courtId;
    private String courtName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String customerName; // guestName / user.fullName
    private String customerPhone;
    private BigDecimal totalPrice;
    private BookingStatus status;
}
