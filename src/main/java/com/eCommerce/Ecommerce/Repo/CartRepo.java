package com.eCommerce.Ecommerce.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eCommerce.Ecommerce.Entities.Cart;
import com.eCommerce.Ecommerce.Entities.User;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {
    Cart findByUser(User user);

    public Cart getUserById(Long id);

}