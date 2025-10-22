package com.sancaulong.booking_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sancaulong.booking_api.dto.AuthResponse;
import com.sancaulong.booking_api.dto.LoginRequest;
import com.sancaulong.booking_api.dto.RegisterRequest;
import com.sancaulong.booking_api.entity.User;
import com.sancaulong.booking_api.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    // Đăng ký
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
            User newUser = authService.registerUser(request);
            
            // 1. Trả về khi thành công
            return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
            
        } catch (RuntimeException e) {
            // 2. Trả về khi thất bại
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        // Không có code nào ở đây
    }
    
    /**
     * API Endpoint cho Đăng nhập (Sign In)
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = authService.loginUser(loginRequest);
            
            // 1. Trả về khi thành công
            return ResponseEntity.ok(authResponse);
            
        } catch (Exception e) {
            // 2. Trả về khi thất bại (sai pass, v.v.)
            return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }
        // Không có code nào ở đây
    }
}