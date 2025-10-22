package com.sancaulong.booking_api.entity;

import java.math.BigDecimal;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

@Entity
@Table(name = "courts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Court {
    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name="description", columnDefinition="TEXT")
    private String description;

    @Column(name="base_price_per_hour", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePricePerHour;

    @Enumerated(EnumType.STRING)  //Luu enum duoi dang string
    @Column(name="status", nullable = false)
    private CourtStatus status;

    //mot (one) san co nhieu (many) lich dat
    
    @OneToMany(mappedBy="court")
    private Set<Booking> bookings;
}