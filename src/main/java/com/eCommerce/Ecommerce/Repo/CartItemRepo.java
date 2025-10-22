package com.eCommerce.Ecommerce.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eCommerce.Ecommerce.Entities.Cart;
import com.eCommerce.Ecommerce.Entities.CartItem;
import com.eCommerce.Ecommerce.Entities.Product;

@Repository
public interface CartItemRepo extends JpaRepository<CartItem, Long> {

    public CartItem findByCartAndProductAndSize(Cart cart, Product product, String size);

}