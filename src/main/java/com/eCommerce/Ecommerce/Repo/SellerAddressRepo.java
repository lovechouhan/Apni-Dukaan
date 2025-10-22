package com.eCommerce.Ecommerce.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eCommerce.Ecommerce.Entities.SellerAddress;

@Repository
public interface SellerAddressRepo extends JpaRepository<SellerAddress, Long> {
}