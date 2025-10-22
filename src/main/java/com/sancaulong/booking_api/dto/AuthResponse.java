package com.sancaulong.booking_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor // <-- Annotation này rất quan trọng
public class AuthResponse {
    private String email;
    private String token; // Token JWT
}