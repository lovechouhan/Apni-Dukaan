package com.eCommerce.Ecommerce.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eCommerce.Ecommerce.Entities.Cart;
import com.eCommerce.Ecommerce.Entities.CartItem;
import com.eCommerce.Ecommerce.Entities.Product;
import com.eCommerce.Ecommerce.Entities.User;
import com.eCommerce.Ecommerce.Repo.CartItemRepo;
import com.eCommerce.Ecommerce.Repo.CartRepo;

@Service
public class CartService {
    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private CartItemRepo cartItemRepo;

    public Cart getCartByUser(User user) {
        return cartRepo.findByUser(user);
    }

    public CartItem updateCart(Long cartID, Long cartItemID, int quantity) {
        Cart cart = cartRepo.findById(cartID).orElse(null);
        CartItem cartItem = cartItemRepo.findById(cartItemID).orElse(null);
        if (cartItem != null) {
            cartItemRepo.delete(cartItem);
        }
        if (cart != null) {
            cart.setTotalItems(cart.getCartItems().size());
            cart.setTotalMRPPrice((int) cart.getCartItems().stream()
                    .mapToDouble(item -> item.getMRPprice() * item.getQuantity())
                    .sum());

            double totalAmount = cart.getCartItems().stream()
                    .mapToDouble(item -> item.getSellingPrice() * item.getQuantity())
                    .sum();
            cart.setTotalAmount(totalAmount);
            cart.setDiscount(cart.getTotalMRPPrice() - (int) totalAmount);
            cartRepo.save(cart);
            return cartItem;
        }

        return null;

    }

    public void addToCart(User user, Product product) {
        Cart cart = cartRepo.findByUser(user);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cartRepo.save(cart); // Save the cart first to get an ID
        }

        // Check if product already exists in cart
        CartItem existingItem = cartItemRepo.findByCartAndProductAndSize(cart, product, product.getSizes().get(0));

        if (existingItem != null) {
            // Update existing item quantity
            existingItem.setQuantity(existingItem.getQuantity() + 1);
            cartItemRepo.save(existingItem);
        } else {
            // Add new item
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            cartItem.setUserId(user.getId());
            cartItem.setMRPprice(product.getMRPprice());
            cartItem.setSellingPrice(product.getSellingPrice());

            cartItem.setSize(product.getSizes().get(0));
            cartItemRepo.save(cartItem);
            cart.getCartItems().add(cartItem);
        }

        // Update cart totals
        cart.setTotalItems(cart.getCartItems().size());
        double currentCartAmount = cart.getTotalAmount();
        // Calculate totals from saved cart items to ensure accuracy
        double totalAmount = cartItemRepo.findByCartAndProductAndSize(cart, product, product.getSizes().get(0))
                .getSellingPrice() * cartItemRepo.findByCartAndProductAndSize(cart, product, product.getSizes().get(0))
                        .getQuantity();

        double currentTotalMRPPrice = cart.getTotalMRPPrice();
        double totalMRPPrice = cartItemRepo.findByCartAndProductAndSize(cart, product, product.getSizes().get(0))
                .getMRPprice() * cartItemRepo.findByCartAndProductAndSize(cart, product, product.getSizes().get(0))
                        .getQuantity();

        cart.setTotalMRPPrice((int) totalMRPPrice + (int)currentTotalMRPPrice);
        cart.setTotalAmount(totalAmount + currentCartAmount);
        cart.setDiscount((int) (totalMRPPrice - totalAmount));

        cartRepo.save(cart);
    }

                

    public void clearCart(User user) {
    Cart cart = getCartByUser(user);
    if(cart != null){
        cart.getCartItems().clear(); // saare items remove
        cart.setTotalAmount(0);
        cart.setDiscount(0);
        cart.setDeliveryCharge(0);
        cartRepo.save(cart); // update cart
    }
    }
}
