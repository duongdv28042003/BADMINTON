package com.sancaulong.booking_api.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component 
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider; 

    @Autowired
    private CustomUserDetailsService customUserDetailsService; // tìm user

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            // 1. Lấy Token từ Request
            String jwt = getJwtFromRequest(request);

            // 2. Validate Token
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                
                // 3. Lấy email từ Token
                String email = tokenProvider.getEmailFromJWT(jwt);

                // 4. Lấy thông tin User (bao gồm cả Role)
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                // 5. Tạo một đối tượng Authentication
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //  "Báo" cho Spring Security biết user này đã được xác thực
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {

            logger.error("Không thể xác thực người dùng.", ex);
        }

        // 7. Chuyển request đi tiếp
        filterChain.doFilter(request, response);
    }

    /**
     *  Lấy Token từ Header (Authorization: Bearer <token>)
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        // Kiểm tra xem có Header "Authorization" và có bắt đầu bằng "Bearer " không
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length()); // Cắt bỏ chữ "Bearer "
        }
        return null;
    }
}