package com.sancaulong.booking_api.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sancaulong.booking_api.dto.PriceDetails;
import com.sancaulong.booking_api.entity.Court;
import com.sancaulong.booking_api.entity.PricingRule;
import com.sancaulong.booking_api.repository.CourtRepository;
import com.sancaulong.booking_api.repository.PricingRuleRepository;

@Service
public class PricingService {

    @Autowired
    private CourtRepository courtRepository;

    @Autowired
    private PricingRuleRepository pricingRuleRepository;
    /**
     * * @param courtId 
     * @param startTime 
     * @param endTime 
     * @return 
     */
    public PriceDetails calculateBookingPriceDetails(Long courtId, LocalDateTime startTime, LocalDateTime endTime) {

        // Lấy thông tin cơ bản
        Court court = courtRepository.findById(courtId)
                .orElseThrow(() -> new RuntimeException("Court not found with id: " + courtId));
        
        // Giá gốc 
        BigDecimal basePricePerHour = court.getBasePricePerHour();
        
        // Lấy tất cả quy tắc đang hoạt động 
        List<PricingRule> activeRules = pricingRuleRepository.findByIsActive(true);

        // Khởi tạo các biến tính tổng
        BigDecimal totalBasePrice = BigDecimal.ZERO;
        BigDecimal finalTotalPrice = BigDecimal.ZERO;

        // Vòng lặp: Lặp qua từng giờ từ startTime đến endTime
        LocalDateTime currentHour = startTime;
        while (currentHour.isBefore(endTime)) {
            
            // Lấy giá gốc của giờ này
            BigDecimal priceOfThisHour = basePricePerHour;
            
            // Cộng vào tổng giá GỐC 
            totalBasePrice = totalBasePrice.add(basePricePerHour);

            // Kiểm tra xem giờ này (currentHour) có thuộc giờ cao điểm không
            LocalTime timeToCheck = currentHour.toLocalTime(); //
            boolean ruleApplied = false;
            
            for (PricingRule rule : activeRules) {
                // Logic: (timeToCheck >= rule.startTime) AND (timeToCheck < rule.endTime)
                if (!timeToCheck.isBefore(rule.getStartTime()) && timeToCheck.isBefore(rule.getEndTime())) {
                    
                    // --- ÁP DỤNG QUY TẮC ---
                    // Ví dụ: 100,000 * 1.2 = 120,000
                    priceOfThisHour = basePricePerHour.multiply(rule.getPriceMultiplier());
                    ruleApplied = true;
                    break; // Giả sử mỗi giờ chỉ có 1 quy tắc được áp dụng
                }
            }

            // Cộng giá (đã tính) của giờ này vào tổng cuối cùng
            finalTotalPrice = finalTotalPrice.add(priceOfThisHour);

            // Chuyển sang giờ tiếp theo
            currentHour = currentHour.plusHours(1);
        } 

        // Tính hệ số trung bình
        BigDecimal averageMultiplier;
        if (totalBasePrice.compareTo(BigDecimal.ZERO) == 0) {
            averageMultiplier = BigDecimal.ONE; // Tránh lỗi chia cho 0
        } else {
            // (Ví dụ: 220,000 / 200,000 = 1.10)
            averageMultiplier = finalTotalPrice.divide(totalBasePrice, 2, RoundingMode.HALF_UP);
        }

        // Trả về kết quả
        return new PriceDetails(totalBasePrice, finalTotalPrice, averageMultiplier);
    }
}