package com.eCommerce.Ecommerce.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.eCommerce.Ecommerce.Entities.Seller;

public interface SellerRepo extends JpaRepository<Seller, Long> {

    Seller findByEmail(String email);

    @Query("SELECT s FROM Seller s")
    List<Seller> getAllSellers();

   
    @Query("SELECT s FROM Seller s WHERE s.SellerName = ?1")
    Seller findSellerByName(String name);
}
