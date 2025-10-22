package com.sancaulong.booking_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sancaulong.booking_api.entity.User;

@Repository

public interface UserRepository extends JpaRepository<User, Long> {
    // Tim User khi dang nhap
    //Select * from users where email = ?
    Optional<User> findByEmail(String email);  
    
    //Kiem tra dang ky
    //Select *from users where phone_number = ?
    Optional<User> findByPhoneNumber(String phoneNumber);

    //Kiem tra email ton tai
    boolean existsByEmail(String email);

    //Kiem tra sdt ton tai
    boolean existsByPhoneNumber(String phoneNumber);
}
