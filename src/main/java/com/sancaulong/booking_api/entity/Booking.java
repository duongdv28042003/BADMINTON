package com.sancaulong.booking_api.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Booking {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    //Nhieu lich dat trong 1 san
    @ManyToOne
    @JoinColumn(name = "court_id", nullable=false)
    private Court court;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable=true)
    private User user;

    // Khach vang lai
    @Column(name = "guest_name")
    private String guestName;

    @Column(name = "guest_phone")
    private String guestPhone;

    @Column(name = "guest_email")
    private String guestEmail;

    // Thong tin lich dat
    @Column(name = "start_time", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime endTime;

    // Thong tin gia
    @Column(name = "base_price", nullable=false, precision=10, scale=2)
    private BigDecimal basePrice;

    @Column(name = "price_multiplier", nullable=false, precision=4, scale=2)
    private BigDecimal priceMultiplier;

    @Column(name = "total_price", nullable=false, precision=10, scale=2)
    private BigDecimal totalPrice;

    // Trang thai
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable=false)
    private BookingStatus status;

    @Column(name="created_at", updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Moi quan he 1-1 voi payment
    // 1 booking chi co 1 payment
    @OneToOne(mappedBy="booking", cascade=CascadeType.ALL)
    private Payment payment;
}
