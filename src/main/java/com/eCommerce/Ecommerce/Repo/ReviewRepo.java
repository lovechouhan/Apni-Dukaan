package com.eCommerce.Ecommerce.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eCommerce.Ecommerce.Entities.Review;

@Repository
public interface ReviewRepo extends JpaRepository<Review, Long> {

   
  
    List<Review> findByProductId(Long productId);
}