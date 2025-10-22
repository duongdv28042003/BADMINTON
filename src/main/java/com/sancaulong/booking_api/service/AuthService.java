package com.sancaulong.booking_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sancaulong.booking_api.dto.AuthResponse;
import com.sancaulong.booking_api.dto.LoginRequest;
import com.sancaulong.booking_api.dto.RegisterRequest;
import com.sancaulong.booking_api.entity.Role;
import com.sancaulong.booking_api.entity.User;
import com.sancaulong.booking_api.repository.UserRepository;
import com.sancaulong.booking_api.security.JwtTokenProvider;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    /**
     * Xử lý logic Đăng ký tài khoản mới
     */
    public User registerUser(RegisterRequest request) {

        // 1. Kiểm tra xem email hoặc SĐT đã tồn tại chưa
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Error: Phone number is already in use!");
        }

        // 2. Tạo đối tượng User mới
        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setFullName(request.getFullName());
        newUser.setPhoneNumber(request.getPhoneNumber());
        newUser.setRole(Role.ROLE_USER); 

        // 3. Lưu vào CSDL và trả về
        return userRepository.save(newUser);
    }
    
    /**
     * Xử lý logic Đăng nhập
     */
    public AuthResponse loginUser(LoginRequest loginRequest) {
        // 1. Xác thực email và mật khẩu (Sẽ ném lỗi nếu sai)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // 2. Nếu xác thực thành công, set vào SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Tạo JWT Token
        String jwt = tokenProvider.generateToken(authentication);
        
        // 4. Trả về Token cho client
        return new AuthResponse(loginRequest.getEmail(), jwt);
    }
}