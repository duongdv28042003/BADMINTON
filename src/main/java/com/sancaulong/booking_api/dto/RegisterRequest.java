package com.sancaulong.booking_api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String email;
    private String password; // Mật khẩu gốc
    private String fullName;
    private String phoneNumber;
}