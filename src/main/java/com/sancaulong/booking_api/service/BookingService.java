package com.sancaulong.booking_api.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sancaulong.booking_api.dto.BookingResponse;
import com.sancaulong.booking_api.dto.GuestBookingRequest;
import com.sancaulong.booking_api.dto.PriceDetails;
import com.sancaulong.booking_api.entity.Booking;
import com.sancaulong.booking_api.entity.BookingStatus;
import com.sancaulong.booking_api.entity.Court;
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
    private PricingService pricingService; 
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public BookingResponse createGuestBooking(GuestBookingRequest request) {

        //  VALIDATION 

        // Kiểm tra xem Sân (Court) có tồn tại không
        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new RuntimeException("San khong ton tai."));

        // 1b. Kiểm tra giờ kết thúc phải sau giờ bắt đầu
        if (request.getEndTime().isBefore(request.getStartTime()) || 
            request.getEndTime().isEqual(request.getStartTime())) {
            throw new RuntimeException("Gio ket thuc phai truoc gio bat dau");
        }

        // KIỂM TRA TRÙNG LỊCH 
        // Gọi hàm "findOverlappingBooking" //
        Optional<Booking> existingBooking = bookingRepository.findOverlappingBooking(
                request.getCourtId(),
                request.getStartTime(),
                request.getEndTime()
        );

        if (existingBooking.isPresent()) {
            // Nếu tìm thấy 1 lịch bị chồng chéo, báo lỗi ngay
            throw new RuntimeException("Thoi gian nay da duoc dat");
        }

        // TÍNH GIÁ ---
        PriceDetails priceDetails = pricingService.calculateBookingPriceDetails(
                request.getCourtId(),
                request.getStartTime(),
                request.getEndTime()
        );

        // TẠO VÀ LƯU BOOKING ---
        // Tạo một đối tượng Booking mới
        Booking newBooking = new Booking();

        // Set thông tin từ request
        newBooking.setCourt(court);
        newBooking.setStartTime(request.getStartTime());
        newBooking.setEndTime(request.getEndTime());

        // Set thông tin khách vãng lai
        newBooking.setUser(null); // user_id = NULL
        newBooking.setGuestName(request.getGuestName());
        newBooking.setGuestPhone(request.getGuestPhone());
        newBooking.setGuestEmail(request.getGuestEmail());

        // Set thông tin giá (từ PricingService)
        newBooking.setBasePrice(priceDetails.getTotalBasePrice());
        newBooking.setPriceMultiplier(priceDetails.getAverageMultiplier());
        newBooking.setTotalPrice(priceDetails.getFinalTotalPrice());
        
        // Set trạng thái ban đầu (Giả sử là CONFIRMED luôn vì chưa làm thanh toán)
        newBooking.setStatus(BookingStatus.CONFIRMED); 
        newBooking.setCreatedAt(LocalDateTime.now());

        // Lưu vào CSDL
        Booking savedBooking = bookingRepository.save(newBooking);

        // TRẢ VỀ RESPONSE ---
        // Chuyển đổi Entity (savedBooking) sang dto (BookingResponse) để trả về
        return mapToBookingResponse(savedBooking);
    }

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
    
    
}