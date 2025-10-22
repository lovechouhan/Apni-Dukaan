package com.eCommerce.Ecommerce.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.eCommerce.Ecommerce.Repo.UserRepo;
import com.eCommerce.Ecommerce.Entities.User;
import com.eCommerce.Ecommerce.Services.ReviewService;

@Controller
@RequestMapping("/user/review")
public class ReviewController {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private ReviewService reviewService;
    
    @PostMapping("/submitReview")
    public String submitReview( @ModelAttribute("review") String review,
                                @ModelAttribute("rating") int rating,
                                @ModelAttribute("productId") Long productId,
                                Model model
    ) {

          Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email;

        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            // For OAuth2 (Google) login
            OAuth2User oauth2User = oauthToken.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else {
            // For regular login
            email = auth.getName();
        }
       
        User user = userRepository.findByEmail(email);
        if (user == null) return "redirect:/login";

        reviewService.saveReview(review, rating, productId, user);

       model.addAttribute("review", review);
       model.addAttribute("rating", rating);
       model.addAttribute("productId", productId);
       model.addAttribute("user", user);

        return "redirect:/user/products/view/" + productId; // Redirect back to the product page
    }
}
