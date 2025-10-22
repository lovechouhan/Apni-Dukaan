package com.eCommerce.Ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import com.eCommerce.Ecommerce.Services.AddressService;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private AddressService addressService;
    
    @PostMapping("/process")
    public String processCheckout(
            @RequestParam String fullName,
            @RequestParam String streetAddress,
            @RequestParam String email,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam String zipCode,
            @RequestParam String mobile,
            @RequestParam String payment,
            Model model
            ) {
                
  
    
        addressService.saveAddress(fullName, streetAddress, city, email, state, zipCode, mobile);
        // Redirect to a confirmation page or payment gateway
        model.addAttribute("fullName", fullName);
        model.addAttribute("streetAddress", streetAddress);
        model.addAttribute("city", city);
        model.addAttribute("state", state);
        model.addAttribute("zipCode", zipCode);
        model.addAttribute("mobile", mobile);
        return "user/payment";
    }
}
