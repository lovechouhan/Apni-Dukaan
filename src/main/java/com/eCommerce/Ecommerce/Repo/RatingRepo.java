package com.eCommerce.Ecommerce.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eCommerce.Ecommerce.Entities.Rating;

@Repository
public interface RatingRepo extends JpaRepository<Rating, Long> {

  
}