package com.sancaulong.booking_api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sancaulong.booking_api.dto.AuthenticatedBookingRequest;
import com.sancaulong.booking_api.dto.AvailabilitySlotDTO;
import com.sancaulong.booking_api.dto.BookingResponse;
import com.sancaulong.booking_api.dto.GuestBookingRequest;
import com.sancaulong.booking_api.dto.PriceDetails;
import com.sancaulong.booking_api.entity.Booking;
import com.sancaulong.booking_api.entity.BookingStatus;
import com.sancaulong.booking_api.entity.Court;
import com.sancaulong.booking_api.entity.Role;
import com.sancaulong.booking_api.entity.User;
import com.sancaulong.booking_api.repository.BookingRepository;
import com.sancaulong.booking_api.repository.CourtRepository;
import com.sancaulong.booking_api.repository.UserRepository;

@Service
public class BookingService {

    private static final int OPENING_HOUR = 5;
    private static final int CLOSING_HOUR = 22;

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CourtRepository courtRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PricingService pricingService;

    /**
     *  Đặt sân cho khách vãng lai (Public)
     */
    @Transactional
    public BookingResponse createGuestBooking(GuestBookingRequest request) {
        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new RuntimeException("Không thấy."));
        if (request.getEndTime().isBefore(request.getStartTime()) || 
            request.getEndTime().isEqual(request.getStartTime())) {
            throw new RuntimeException("Thời gian kết thúc phải sau thời gian bắt đầu.");
        }
        Optional<Booking> existingBooking = bookingRepository.findOverlappingBooking(
                request.getCourtId(), request.getStartTime(), request.getEndTime()
        );
        if (existingBooking.isPresent()) {
            throw new RuntimeException("Đã được đặt.");
        }
        PriceDetails priceDetails = pricingService.calculateBookingPriceDetails(
                request.getCourtId(), request.getStartTime(), request.getEndTime()
        );
        Booking newBooking = new Booking();
        newBooking.setCourt(court);
        newBooking.setStartTime(request.getStartTime());
        newBooking.setEndTime(request.getEndTime());
        newBooking.setUser(null);
        newBooking.setGuestName(request.getGuestName());
        newBooking.setGuestPhone(request.getGuestPhone());
        newBooking.setGuestEmail(request.getGuestEmail());
        newBooking.setBasePrice(priceDetails.getTotalBasePrice());
        newBooking.setPriceMultiplier(priceDetails.getAverageMultiplier());
        newBooking.setTotalPrice(priceDetails.getFinalTotalPrice());
        newBooking.setStatus(BookingStatus.CONFIRMED); 
        newBooking.setCreatedAt(LocalDateTime.now());
        Booking savedBooking = bookingRepository.save(newBooking);
        return mapToBookingResponse(savedBooking);
    }
    
    /**
     * Đặt sân cho user đã đăng nhập (Private)
     */
    @Transactional
    public BookingResponse createAuthenticatedBooking(AuthenticatedBookingRequest request) {
        // 1. Lấy user từ Token
        User currentUser = getCurrentUserPrincipal();
        
        // 2. Kiểm tra 
        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy."));
        if (request.getEndTime().isBefore(request.getStartTime()) || 
            request.getEndTime().isEqual(request.getStartTime())) {
            throw new RuntimeException("Lỗi thời gian.");
        }
        Optional<Booking> existingBooking = bookingRepository.findOverlappingBooking(
                request.getCourtId(), request.getStartTime(), request.getEndTime()
        );
        if (existingBooking.isPresent()) {
            throw new RuntimeException("Đã được đặt.");
        }

        // Tính giá
        PriceDetails priceDetails = pricingService.calculateBookingPriceDetails(
                request.getCourtId(), request.getStartTime(), request.getEndTime()
        );

        // Tạo và Lưu Booking
        Booking newBooking = new Booking();
        newBooking.setCourt(court);
        newBooking.setStartTime(request.getStartTime());
        newBooking.setEndTime(request.getEndTime());
        
        // Gán User (thay vì Guest)
        newBooking.setUser(currentUser); 
        newBooking.setGuestName(null);
        newBooking.setGuestPhone(null);
        newBooking.setGuestEmail(null);

        newBooking.setBasePrice(priceDetails.getTotalBasePrice());
        newBooking.setPriceMultiplier(priceDetails.getAverageMultiplier());
        newBooking.setTotalPrice(priceDetails.getFinalTotalPrice());
        newBooking.setStatus(BookingStatus.CONFIRMED); 
        newBooking.setCreatedAt(LocalDateTime.now());
        
        Booking savedBooking = bookingRepository.save(newBooking);
        
        return mapToBookingResponse(savedBooking);
    }

    /**
     * Lấy các khung giờ trống (Public)
     */
    public List<AvailabilitySlotDTO> getAvailableSlots(Long courtId, LocalDate date) {
        LocalDateTime dayStart = date.atTime(OPENING_HOUR, 0); 
        LocalDateTime dayEnd = date.atTime(CLOSING_HOUR, 0);   
        List<Booking> existingBookings = bookingRepository.findBookingsForDay(courtId, dayStart, dayEnd);
        List<AvailabilitySlotDTO> slots = new ArrayList<>();
        LocalDateTime currentSlotStart = dayStart;
        while (currentSlotStart.isBefore(dayEnd)) {
            LocalDateTime currentSlotEnd = currentSlotStart.plusHours(1);
            boolean isAvailable = true;
            for (Booking booked : existingBookings) {
                if (currentSlotStart.isBefore(booked.getEndTime()) && 
                    currentSlotEnd.isAfter(booked.getStartTime())) {
                    isAvailable = false;
                    break;
                }
            }
            PriceDetails priceDetails = pricingService.calculateBookingPriceDetails(
                courtId, currentSlotStart, currentSlotEnd
            );
            slots.add(new AvailabilitySlotDTO(
                currentSlotStart, currentSlotEnd, priceDetails.getFinalTotalPrice(), isAvailable
            ));
            currentSlotStart = currentSlotStart.plusHours(1);
        }
        return slots;
    }

    /**
     * Lấy lịch sử đặt sân của tôi (Private)
     */
    public List<BookingResponse> getMyBookings() {
        User currentUser = getCurrentUserPrincipal(); 
        List<Booking> bookings = bookingRepository.findByUserId(currentUser.getId());
        return bookings.stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());
    }

    /**
     * Hủy lịch đặt (Private)
     */
    @Transactional
    public BookingResponse cancelBooking(Long bookingId) {
        User currentUser = getCurrentUserPrincipal();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy."));
        if (booking.getUser() == null || 
            (!booking.getUser().getId().equals(currentUser.getId()) && 
             currentUser.getRole() != Role.ROLE_ADMIN)) {
            throw new SecurityException("Bạn không có quyền hủy.");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Đã hủy.");
        }
        if (booking.getStartTime().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new RuntimeException("Không thể hủy quá muộn.");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        Booking cancelledBooking = bookingRepository.save(booking);
        return mapToBookingResponse(cancelledBooking);
    }
    

    // === CÁC HÀM HELPER (HỖ TRỢ) ===
    private BookingResponse mapToBookingResponse(Booking booking) {

        String customerName = (booking.getUser() != null) 
                                ? booking.getUser().getFullName() 
                                : booking.getGuestName();
        String customerPhone = (booking.getUser() != null)
                                ? booking.getUser().getPhoneNumber()
                                : booking.getGuestPhone();
        return BookingResponse.builder()
                .bookingId(booking.getId())
                .courtId(booking.getCourt().getId())
                .courtName(booking.getCourt().getName())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .customerName(customerName)
                .customerPhone(customerPhone)
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .build();
    }

    private User getCurrentUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new SecurityException("Người dùng chưa xác thực.");
        }
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Đã xác thực người dùng."));
    }
}