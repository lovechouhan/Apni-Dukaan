package com.eCommerce.Ecommerce.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eCommerce.Ecommerce.Domain.UserRoles;
import com.eCommerce.Ecommerce.Entities.Orders;
import com.eCommerce.Ecommerce.Entities.Seller;
import com.eCommerce.Ecommerce.Entities.User;
import com.eCommerce.Ecommerce.Entities.VerificationCode;
import com.eCommerce.Ecommerce.Form.SellerForm;
import com.eCommerce.Ecommerce.Services.CloudinaryImageService;
import com.eCommerce.Ecommerce.Services.OrderService;
import com.eCommerce.Ecommerce.Services.ProductService;
import com.eCommerce.Ecommerce.Services.SellerService;
import com.eCommerce.Ecommerce.Services.SellerSettingsService;
import com.eCommerce.Ecommerce.Services.UserServices;
import com.eCommerce.Ecommerce.Services.VerificationCodeService;
import com.eCommerce.Ecommerce.dto.SellerSettingsDTO;

@Controller
@RequestMapping("/sellers")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private VerificationCodeService verificationService;

    @Autowired
    private UserServices userServices;

    @Autowired
    private ProductService productService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SellerSettingsService sellerSettingsService;

    @Autowired
    private CloudinaryImageService imageService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        int sellerProductCount = productService.getProductCountForCurrentSeller();
        int orderCount = orderService.getOrderCountForCurrentSeller();

        List<Orders> sellerDeliveredOrders = orderService.getAllOrdersForCurrentSeller();
        model.addAttribute("sellerDeliveredOrders", sellerDeliveredOrders);

        int sellerTotalEarnings = orderService.getTotalEarningsForCurrentSeller();
        model.addAttribute("sellerTotalEarnings", sellerTotalEarnings);

        model.addAttribute("orderCount", orderCount);
        System.out.println("Product count for current seller: " + sellerProductCount);
        model.addAttribute("sellerProductCount", sellerProductCount);
        return "seller/sellerdashboard";
    }

    @GetMapping("/sellerRegister")
    public String showSellerRegistrationForm(Model model) {
        model.addAttribute("sellerForm", new SellerForm());
        return "seller/sellerreg";
    }

    @PostMapping("/register")
    public String registerSeller(@ModelAttribute("sellerForm") SellerForm sellerForm, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            // Create new seller from form data
            Seller seller = new Seller();
            seller.setSellerName(sellerForm.getSellerName());
            seller.setEmail(sellerForm.getEmail());
            seller.setPassword(passwordEncoder.encode(sellerForm.getPassword()));
            seller.setPhoneNumber(sellerForm.getPhoneNumber());
            seller.setGSTIN(sellerForm.getGSTIN());
            seller.setRole(UserRoles.SELLER);

            // Handle profile picture upload
            if (sellerForm.getProfilePic() != null && !sellerForm.getProfilePic().isEmpty()) {
                try {

                    Map data = imageService.upload(sellerForm.getProfilePic());
                    String fileURL = data.get("url").toString();
                    seller.setProfileImageUrl(fileURL);
                } catch (Exception e) {
                    model.addAttribute("error", "Failed to upload profile picture: " + e.getMessage());
                    return "seller/sellerreg";
                }
            }
            // Set business details, bank details, and pickup address from the form
            seller.setBussinessDetails(sellerForm.getBussinessDetails());
            seller.setBankDetails(sellerForm.getBankDetails());
            seller.setPickupAddress(sellerForm.getPickupAddress());

            User user = new User();
            user.setName(sellerForm.getSellerName());
            user.setPassword(passwordEncoder.encode(sellerForm.getPassword()));
            user.setEmail(sellerForm.getEmail());
            user.setPhoneNumber(sellerForm.getPhoneNumber());
            user.setEnabled(false); // User is disabled until OTP verification
            user.setRole(UserRoles.SELLER); // Set role as SELLER

            userServices.saveUser(user);
            sellerService.saveSeller(seller);
            redirectAttributes.addFlashAttribute("message", "Registration successful! Please verify your email.");
            String emailParam = URLEncoder.encode(sellerForm.getEmail(), StandardCharsets.UTF_8);
            return "redirect:/sellers/verify-seller-otp?email=" + emailParam;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "seller/sellerreg";
        }
    }

    // Serve Seller OTP page via GET so the address bar shows the correct URL
    @GetMapping("/verify-seller-otp")
    public String getSellerOtpPage(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "seller/otpseller";
    }

    @PostMapping("/verify-seller-otp")
    public String verifyOtpforseller(@RequestParam("email") String email, @RequestParam("otp") int otp,
            RedirectAttributes redirectAttributes) {
        try {
            Seller seller = sellerService.getSellerByEmail(email);
            User user = userServices.getUserByEmail(email);
            System.out.println("Verifying OTP for email: " + email);

            if (seller == null) {
                System.out.println("Seller not found with email: " + email);
                return "redirect:/login?error=SellerNotFound";
            }

            System.out.println("Stored OTP: " + seller.getOtp() + ", Received OTP: " + otp);
            if (seller.getOtp() == otp) {
                seller.setIsemailVerified(true);
                // user.setIsemailVerified(true);
                user.setEnabled(true);
                seller.setOtp(0); // Clear the OTP after successful verification
                seller.setEnabled(true);
                sellerService.updateSellerStatus(seller);
                userServices.updateUserstatus(user);
                System.out.println("Seller verified and enabled: " + seller.getEmail());

                // Programmatically authenticate seller so navbar and role-based UI appear
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole()));
                UserDetails principal = org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                        .password("")
                        .authorities(authorities)
                        .build();
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal,
                        null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Redirect to main so URL updates
                redirectAttributes.addFlashAttribute("alertType", "success");
                redirectAttributes.addFlashAttribute("message", "Seller verified successfully!");
                redirectAttributes.addFlashAttribute("success", "login to continue ");

                return "redirect:/user/main";
            }

            // If OTP doesn't match
            System.out.println("OTP verification failed for user: " + email);

            return "seller/otpseller";

        } catch (Exception e) {
            System.out.println("Error during OTP verification: " + e.getMessage());
            e.printStackTrace();

            return "seller/otpseller";
        }
    }

    public ResponseEntity<?> loginSeller(@RequestBody VerificationCode request) {

        String email = request.getEmail();
        String otp = request.getOtp();
        Seller seller = sellerService.getSellerByEmail(email); // Assuming getSellerByEmail returns null if not found
        if (seller != null && seller.getOtp() == Integer.parseInt(otp)) { // Assuming Seller has an OTP field
            return ResponseEntity.ok("Seller logged in successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid email or OTP");
        }

    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("email") String email, @RequestParam("otp") int otp,
            RedirectAttributes redirectAttributes) {
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

                // After successful verification, redirect to login page with success message
                redirectAttributes.addFlashAttribute("alertType", "success");
                redirectAttributes.addFlashAttribute("message", "Seller verified successfully!");
                return "user/main";
            }

            // If OTP doesn't match
            System.out.println("OTP verification failed for user: " + email);
            redirectAttributes.addFlashAttribute("alertType", "error");
            redirectAttributes.addFlashAttribute("message", "Invalid OTP. Please try again.");
            return "seller/otpseller";

        } catch (Exception e) {
            System.out.println("Error during OTP verification: " + e.getMessage());
            e.printStackTrace();

            return "seller/otpseller";
        }
    }

    @GetMapping("/settings")
    public String getSettings(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        SellerSettingsDTO settings = sellerSettingsService.getSellerSettings(email);
        model.addAttribute("settings", settings);
        return "seller/settings";
    }

    @PostMapping("/settings")
    public String updateSettings(@ModelAttribute SellerSettingsDTO settings, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        settings = sellerSettingsService.updateSellerSettings(email, settings);
        model.addAttribute("settings", settings);
        model.addAttribute("message", "Settings updated successfully");
        return "seller/settings";
    }

    @GetMapping("/settings/business")
    public String getBusinessSettings(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        SellerSettingsDTO settings = sellerSettingsService.getSellerSettings(email);
        model.addAttribute("settings", settings);
        return "seller/business-settings";
    }

    @GetMapping("/settings/notifications")
    public String getNotificationSettings(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        SellerSettingsDTO settings = sellerSettingsService.getSellerSettings(email);
        model.addAttribute("settings", settings);
        return "seller/notification-settings";
    }

    @GetMapping("/settings/payout")
    public String getPayoutSettings(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        SellerSettingsDTO settings = sellerSettingsService.getSellerSettings(email);
        model.addAttribute("settings", settings);
        return "seller/payout-settings";
    }

    @GetMapping("/ordersDelivered/{id}")
    public String getDeliveredOrders(@PathVariable Long id, Model model) {

        System.out.println("Fetching delivered orders for order ID: " + id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email;
        if (auth instanceof UsernamePasswordAuthenticationToken) {
            email = auth.getName();
        } else {
            return "redirect:/login";
        }

        // Seller seller = sellerService.getSellerByEmail(email);
        Orders deliverOrder = orderService.findOrderById(id);

        System.out.println("Delivered Order Details: " + deliverOrder);
        model.addAttribute("deliverOrder", deliverOrder);
        // also add as 'order' for template compatibility

        return "seller/delivered-ordersdetails";
    }

    @GetMapping("/deliveredOrders")
    public String getDeliveredOrdersList(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email;
        if (auth instanceof UsernamePasswordAuthenticationToken) {
            email = auth.getName();
        } else {
            return "redirect:/login";
        }

        List<Orders> deliveredOrders = orderService.getAllOrdersForCurrentSeller();
        model.addAttribute("deliveredOrders", deliveredOrders);

        return "seller/delivered-orders";
    }
}
