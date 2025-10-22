package com.sancaulong.booking_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sancaulong.booking_api.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Tìm thanh toán dựa trên mã lịch đặt
    // SELECT * FROM payments WHERE booking_id = ?
    Optional<Payment> findByBookingId(Long bookingId);
}