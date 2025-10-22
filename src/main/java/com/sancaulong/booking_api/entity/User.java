package com.sancaulong.booking_api.entity;

import java.time.LocalDateTime;

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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor



public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique= true)
    private String email;

    @Column(name= "password", nullable=false)
    private String password;

    @Column(name="full_name", nullable=false)
    private String fullName;

    @Column(name="phone_number", nullable=false, unique=true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable=false)
    private Role role;

    @Column(name="created_at", updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt = LocalDateTime.now(); // tu gan gio tao

    // 1 user co the co nhieu lich dat
    @OneToMany(mappedBy="user")
    private Set<Booking> booking;
}
