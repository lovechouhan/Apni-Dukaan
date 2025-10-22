package com.eCommerce.Ecommerce.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eCommerce.Ecommerce.Entities.VerificationCode;

public interface  VerificationCodeRepo extends JpaRepository<VerificationCode, Long> {

    VerificationCode findByEmail(String email);
    
    
}
