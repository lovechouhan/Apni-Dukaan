package com.eCommerce.Ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eCommerce.Ecommerce.Entities.Cart;
import com.eCommerce.Ecommerce.Entities.User;
import com.eCommerce.Ecommerce.Repo.UserRepo;
import com.eCommerce.Ecommerce.Services.CartService;
import com.eCommerce.Ecommerce.Services.RazorPayService;
import com.razorpay.RazorpayException;

@RestController
@RequestMapping("/api/payments")
public class RazorpayController {

    @Autowired
    private CartService cartService;

    @Autowired
    private RazorPayService razorPayService;

    @Autowired
    private UserRepo userRepo;

    @PostMapping("/create-order")
public String createOrder() throws RazorpayException {
  
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email;
    
    if (auth instanceof OAuth2AuthenticationToken oauthToken) {
        OAuth2User oauth2User = oauthToken.getPrincipal();
        email = oauth2User.getAttribute("email");
    } else {
        email = auth.getName();
    }
    
    User user = userRepo.findByEmail(email);
    if (user == null) {
        throw new IllegalStateException("User not logged in");
    }

    // Get user's cart
    Cart currentCart = cartService.getCartByUser(user);
    if (currentCart == null) {
        throw new IllegalStateException("No active cart found");
    }

    int amount = (int) ((currentCart.getTotalAmount() - currentCart.getDiscount() + currentCart.getDeliveryCharge())); // convert to paise

 

    String receiptId = "order_rcptid_11" + System.currentTimeMillis();
    String order = razorPayService.createOrder(amount, "INR", receiptId);
    

    return order;
}




@PostMapping("/payment-success")
public String paymentSuccess(@RequestParam String paymentId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email;
    
    if (auth instanceof OAuth2AuthenticationToken oauthToken) {
        OAuth2User oauth2User = oauthToken.getPrincipal();
        email = oauth2User.getAttribute("email");
    } else {
        email = auth.getName();
    }

     User user = userRepo.findByEmail(email);
    if(user == null){
        throw new IllegalStateException("User not logged in");
    }
    if(user != null){
        cartService.clearCart(user); // cart clear karo
    }

    return "Payment successful and cart cleared!";
}

}
