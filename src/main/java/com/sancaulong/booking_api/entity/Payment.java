package com.sancaulong.booking_api.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Payment {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    // 1 thanh toan thuoc ve 1 lich dat
    @OneToOne
    @JoinColumn(name="booking_id", nullable=false, unique=true)
    private Booking booking;

    @Column(name="payment_method", length = 50)
    private String paymentMethod;

    @Column(name="amount", nullable=false, precision=10, scale=2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable=false)
    private PaymentStatus status;

    @Column(name="transaction_code")
    private String transactionCode;

    @Column(name = "created_at", updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;
}
