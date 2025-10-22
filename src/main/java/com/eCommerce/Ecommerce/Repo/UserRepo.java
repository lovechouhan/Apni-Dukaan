package com.eCommerce.Ecommerce.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.eCommerce.Ecommerce.Entities.User;

public interface UserRepo extends JpaRepository<User, Long> {
   
    User findByEmail(String email);

    Optional<User> findById(Long id);

    @Query("SELECT u FROM User u WHERE u.otp = ?1")
    User findByOtp(int otp);

}
