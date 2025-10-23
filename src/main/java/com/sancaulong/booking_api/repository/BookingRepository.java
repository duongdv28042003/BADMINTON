package com.sancaulong.booking_api.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sancaulong.booking_api.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // SELECT * FROM bookings WHERE user_id = ?
    List<Booking> findByUserId(Long userId);

    // SELECT * FROM bookings WHERE start_time >= ? AND end_time <= ?
    List<Booking> findByStartTimeAfterAndEndTimeBefore(LocalDateTime start, LocalDateTime end);

    // SELECT * FROM bookings WHERE court_id = ? AND start_time >= ? AND end_time <= ?
    List<Booking> findByCourtIdAndStartTimeAfterAndEndTimeBefore(Long courtId, LocalDateTime start, LocalDateTime end);

    /* 
     * Logic: (StartA < EndB) và (EndA > StartB)
     */
    @Query("SELECT b FROM Booking b WHERE b.court.id = :courtId " +
           "AND b.status != 'CANCELLED' " + // Chỉ kiểm tra lịch chưa bị hủy
           "AND b.startTime < :endTime " +
           "AND b.endTime > :startTime")
    Optional<Booking> findOverlappingBooking(
            @Param("courtId") Long courtId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    // Tìm các lịch đặt (KHÔNG BỊ HỦY) của 1 sân trong 1 khoảng thời gian (1 ngày)
    @Query("SELECT b FROM Booking b WHERE b.court.id = :courtId " +
           "AND b.status != 'CANCELLED' " +
           "AND b.startTime < :dayEnd " +
           "AND b.endTime > :dayStart")
    List<Booking> findBookingsForDay(
            @Param("courtId") Long courtId,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd
    );
    
}