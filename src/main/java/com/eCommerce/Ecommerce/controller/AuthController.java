package com.eCommerce.Ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Map;
import com.eCommerce.Ecommerce.Domain.UserRoles;
import com.eCommerce.Ecommerce.Entities.Cart;
import com.eCommerce.Ecommerce.Entities.Provider;
import com.eCommerce.Ecommerce.Entities.User;
import com.eCommerce.Ecommerce.Form.UserForm;
import com.eCommerce.Ecommerce.Services.CartService;
import com.eCommerce.Ecommerce.Services.UserServices;
import com.eCommerce.Ecommerce.Services.VerificationCodeService;
import com.eCommerce.Ecommerce.Services.CloudinaryImageService;

@Controller

public class AuthController {

    @Autowired
    private UserServices userServices;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @Autowired
    VerificationCodeService verificationService;

    @Autowired
    private CloudinaryImageService imageService;

    @Autowired
    private CartService cartService;

    @Autowired
    private com.eCommerce.Ecommerce.Repo.CartRepo cartRepo;

    // For rendering Thymeleaf pages
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("userForm") UserForm userForm, Model model,
            RedirectAttributes redirectAttributes) throws Exception {
        try {
            // Create user after OTP verification

            
            // int otp = verificationService.getOtp(userForm.getEmail());
            User user = new User();
            user.setName(userForm.getName());
            user.setPassword(passwordEncoder.encode(userForm.getPassword()));
            user.setEmail(userForm.getEmail());
            user.setPhoneNumber(userForm.getPhoneNumber());
            if (userForm.getProfilePic() != null && !userForm.getProfilePic().isEmpty()) {

            Map data = imageService.upload(userForm.getProfilePic());
            String fileURL = data.get("url").toString();
            user.setProfileImageUrl(fileURL);
            }
            user.setEnabled(false); // User is disabled until OTP verification
            user.setProvider(Provider.SELF); // Default provider
            user.setRole(UserRoles.USER); // Default role

            // Generate and send OTP
            int n = verificationService.generateAndSendOtp(userForm.getEmail());
          
            if (n != -1) {
                user.setOtp(n);
            }
           
            User savedUser = userServices.saveUser(user);

            Cart cart = new Cart();
            cart.setUser(savedUser);
            cartRepo.save(cart);

            // Redirect to OTP page with email param so URL updates
            String emailParam = URLEncoder.encode(userForm.getEmail(), StandardCharsets.UTF_8);
            redirectAttributes.addFlashAttribute("message", "Registration successful! Please verify your email.");
            return "redirect:/otp?email=" + emailParam;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            String emailParam = URLEncoder.encode(userForm.getEmail(), StandardCharsets.UTF_8);
            return "redirect:/otp?email=" + emailParam;
        }

    }

    // Serve OTP page via GET so the address bar shows the correct URL
    @GetMapping("/otp")
    public String getOtpPage(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "otp";
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Thymeleaf login.html
    }

    @ExceptionHandler(BadCredentialsException.class)
    public String exceptionHandler(Model model) {
        model.addAttribute("error", "Credentials Invalid !!");
        return "login";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("email") String email, @RequestParam("otp") int otp) {
        try {
            User user = userServices.getUserByEmail(email);
            System.out.println("Verifying OTP for email: " + email);

            if (user == null) {
                System.out.println("User not found with email: " + email);
                return "redirect:/login?error=UserNotFound";
            }

            System.out.println("Stored OTP: " + user.getOtp() + ", Received OTP: " + otp);
            if (user.getOtp() == otp) {
                user.setEnabled(true);
                user.setOtp(0); // Clear the OTP after successful verification
                userServices.updateUserstatus(user);
                System.out.println("User verified and enabled: " + user.getEmail());

                // Create cart for verified user if not exists
                if (cartService.getCartByUser(user) == null) {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    cartRepo.save(cart);
                }

                // Programmatically authenticate the user so navbar and role-based UI appear
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole()));
                UserDetails principal = org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                        .password("")
                        .authorities(authorities)
                        .build();
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal,
                        null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Redirect to main so URL updates
                return "redirect:/user/main";
            }

            // If OTP doesn't match
            System.out.println("OTP verification failed for user: " + email);

            return "otp";

        } catch (Exception e) {
            System.out.println("Error during OTP verification: " + e.getMessage());
            e.printStackTrace();

            return "otp";
        }
    }

}