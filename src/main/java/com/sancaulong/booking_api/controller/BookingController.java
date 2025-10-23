package com.sancaulong.booking_api.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sancaulong.booking_api.dto.AuthenticatedBookingRequest;
import com.sancaulong.booking_api.dto.AvailabilitySlotDTO;
import com.sancaulong.booking_api.dto.BookingResponse;
import com.sancaulong.booking_api.dto.GuestBookingRequest;
import com.sancaulong.booking_api.service.BookingService;

@RestController
@RequestMapping("/api/v1/bookings") 
public class BookingController {

    @Autowired
    private BookingService bookingService;

    /**
     * Đặt sân cho khách vãng lai (Public)
     */
    @PostMapping("/public/guest")
    public ResponseEntity<?> createGuestBooking(@RequestBody GuestBookingRequest request) {
        try {
            BookingResponse response = bookingService.createGuestBooking(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * User đã đăng nhập tự đặt sân (Private - Cần Token)
     */
    @PostMapping("/secure/create")
    public ResponseEntity<?> createAuthenticatedBooking(@RequestBody AuthenticatedBookingRequest request) {
        try {
            BookingResponse response = bookingService.createAuthenticatedBooking(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Xem lịch trống theo ngày (Public)
     */
    @GetMapping("/public/availability/{courtId}")
    public ResponseEntity<List<AvailabilitySlotDTO>> getAvailableSlots(
            @PathVariable Long courtId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<AvailabilitySlotDTO> slots = bookingService.getAvailableSlots(courtId, date);
        return ResponseEntity.ok(slots);
    }

    /**
     * Xem lịch sử đặt sân của tôi (Private - Cần Token)
     */
    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponse>> getMyBookings() {
        List<BookingResponse> bookings = bookingService.getMyBookings();
        return ResponseEntity.ok(bookings);
    }

    /**
     * Hủy lịch (Private - Cần Token)
     */
    @PutMapping("/cancel/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        try {
            BookingResponse response = bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN); 
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); 
        }
    }
}