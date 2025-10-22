package com.sancaulong.booking_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sancaulong.booking_api.dto.BookingResponse;
import com.sancaulong.booking_api.dto.GuestBookingRequest;
import com.sancaulong.booking_api.service.BookingService;

@RestController 
@RequestMapping("/api/v1/bookings") 
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/public/guest")
    public ResponseEntity<?> createGuestBooking(@RequestBody GuestBookingRequest request) {
        
        try {
            // Gọi BookingService 
            BookingResponse response = bookingService.createGuestBooking(request);
            
            //  Nếu thành công, trả về HTTP Status 201 (Created)
            //    kèm thông tin lịch đặt (BookingResponse)
            return new ResponseEntity<>(response, HttpStatus.CREATED);
            
        } catch (RuntimeException e) {
            
            //  Nếu BookingService ném ra lỗi (ví dụ: "Trùng lịch", "Sân không tồn tại")
            //    Trả về HTTP Status 400 (Bad Request) kèm thông báo lỗi
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}