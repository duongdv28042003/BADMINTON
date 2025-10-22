package com.sancaulong.booking_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sancaulong.booking_api.entity.Court;

@Repository

public interface CourtRepository extends JpaRepository<Court, Long> {
    
}
