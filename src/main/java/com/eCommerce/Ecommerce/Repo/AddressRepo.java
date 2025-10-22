package com.eCommerce.Ecommerce.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eCommerce.Ecommerce.Entities.Address;

public interface AddressRepo extends JpaRepository<Address, Long> {

}
