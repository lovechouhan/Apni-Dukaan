package com.eCommerce.Ecommerce.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eCommerce.Ecommerce.Entities.User;

@Service
public class VerificationCodeService {

    @Autowired
    private UserServices userServices;
    @Autowired
    private EmailService emailService;

    public boolean verifyOtp(String email, int otp) {
        User user = userServices.getUserByEmail(email);
        if (user != null) {
            return user.getOtp() == otp;
        }
        return false;
    }

    public int generateAndSendOtp(String email) {
        // Logic to generate OTP and send it via email
        int otp = (int) (Math.random() * 9000) + 1000; // Generate a 4-digit OTP

        System.out.println("Generated OTP: " + otp); // For testing purposes, print the OTP to the console
        // Fetch the user by email
        User user = userServices.getUserByEmail(email);
        if (user != null) {
            user.setOtp(otp); // Make sure your User entity has a setOtp(int) method and an otp field
            userServices.updateUserstatus(user);
        }
        String subject = "Your OTP Code";
        String text = "Your OTP code is: " + otp + ". It is valid for 10 minutes.";
        try {
            emailService.sendVerificationEmail(email, String.valueOf(otp), subject, text);
            return otp;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
          
        }
        

    }

}
