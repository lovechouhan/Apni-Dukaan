package com.eCommerce.Ecommerce.Services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eCommerce.Ecommerce.Entities.Cart;
import com.eCommerce.Ecommerce.Entities.CartItem;
import com.eCommerce.Ecommerce.Entities.Product;
import com.eCommerce.Ecommerce.Entities.User;
import com.eCommerce.Ecommerce.Exceptions.CartException;
import com.eCommerce.Ecommerce.Exceptions.ProductExceptions;
import com.eCommerce.Ecommerce.Exceptions.UserException;
import com.eCommerce.Ecommerce.Repo.CartItemRepo;
import com.eCommerce.Ecommerce.Repo.CartRepo;

@Service
public class CartItemService {

    @Autowired
    private CartRepo cartRepo;
    @Autowired
    private CartService cartService;

    @Autowired
    private CartItemRepo cartItemRepo;

    public CartItem addCartItem(User user, Product product, String size, int quantity) throws UserException, ProductExceptions, CartException {
        Cart cart = cartService.getCartByUser(user);
        CartItem isPresent = cartItemRepo.findByCartAndProductAndSize(cart, product, size);
        if(isPresent==null){
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setSize(size);
            cartItem.setQuantity(quantity);
            cartItem.setUserId(user.getId());

            int TotalPrice =  quantity * product.getSellingPrice();
            cartItem.setSellingPrice(TotalPrice);
            return cartItemRepo.save(cartItem);
        }
        return isPresent;
    }

    public Cart findUserCart(User user){
        Cart cart = cartRepo.getUserById(user.getId());

        int TotalPrice = 0;
        int TotalDiscountPrice = 0;
        int TotalItems = 0;

        for(CartItem cartItem : cart.getCartItems()){
            TotalPrice += cartItem.getProduct().getSellingPrice() * cartItem.getQuantity();
            TotalDiscountPrice += cartItem.getSellingPrice();
            TotalItems += cartItem.getQuantity();
        }

        cart.setTotalMRPPrice(TotalPrice); // This was a statement, not an error.
        cart.setTotalItems(TotalItems);
        cart.setTotalAmount(TotalDiscountPrice);
        cart.setDiscount(TotalPrice - TotalDiscountPrice);

        return cart;
    }
}