package com.eCommerce.Ecommerce.controller;

/**
 * Base URL: http://localhost:8080/user
 * This controller handles user-related web pages and operations
 */

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eCommerce.Ecommerce.Entities.Cart;
import com.eCommerce.Ecommerce.Entities.Orders;
import com.eCommerce.Ecommerce.Entities.Product;
import com.eCommerce.Ecommerce.Entities.Review;
import com.eCommerce.Ecommerce.Entities.Seller;
import com.eCommerce.Ecommerce.Entities.User;
import com.eCommerce.Ecommerce.Repo.ProductRepo;
import com.eCommerce.Ecommerce.Services.CartService;
import com.eCommerce.Ecommerce.Services.EmailService;
import com.eCommerce.Ecommerce.Services.OrderService;
import com.eCommerce.Ecommerce.Services.ProductService;
import com.eCommerce.Ecommerce.Services.ReviewService;
import com.eCommerce.Ecommerce.Services.SellerService;
import com.eCommerce.Ecommerce.Services.UserServices;
import com.eCommerce.Ecommerce.Services.UserSettingsService;

import jakarta.mail.MessagingException;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private ProductService productService;

    @Autowired
    private SellerService sellerService;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private com.eCommerce.Ecommerce.Repo.UserRepo userRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserServices userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserSettingsService userSettingsService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
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
        if (user == null) {
            return "redirect:/login";
        }
        int userProductCount = userService.getProductCount();
        int userspendings = userService.getTotalSpendings();
        List<Orders> userOrders = orderService.getAllOrders(user.getId());

       
        model.addAttribute("orders", userOrders);
        model.addAttribute("userTotalSpendings", userspendings);
        model.addAttribute("userProductCount", userProductCount);
        return "user/userdashboard";
    }


    @GetMapping("/base")
    public String base(Model model) {
        return "base";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contactus";
    }

    @PostMapping("/contact/submit")
    public String submitContactForm(@RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("message") String message,
            RedirectAttributes redirectAttributes)  {
        // Handle form submission logic here
       try {
        emailService.receviedquery(name, email, message);
        // base.html listens for 'message' (success) or 'alert*' keys
    
        // Optionally set the more detailed alert API used in base.html
        redirectAttributes.addFlashAttribute("alertTitle", "Success");
        redirectAttributes.addFlashAttribute("alertMessage", "Your message has been sent successfully!");
        redirectAttributes.addFlashAttribute("alertType", "success");
        return "redirect:/user/contact";
       } catch (MessagingException e) {
        redirectAttributes.addFlashAttribute("message", "Failed to send your message. Please try again later.");
        // Optionally set the more detailed alert API used in base.html
        redirectAttributes.addFlashAttribute("alertTitle", "Error");
        redirectAttributes.addFlashAttribute("alertMessage", "Failed to send your message. Please try again later.");
        redirectAttributes.addFlashAttribute("alertType", "error");
        return "redirect:/user/contact";
       }
    }

    @GetMapping("/products")
    public String viewProducts1() {
        return "user/product";
    }

    @GetMapping("/details/{id}")
    public String viewProductDetail(@PathVariable("id") Long id, Model model) {
        // Product product = productService.getProductById(id);
        // model.addAttribute("product", product);
        // return "user/productDetail";
        try {
            Product product = productService.getProductById(id);
            if (product == null) {
                model.addAttribute("error", "Product not found");
                return "user/productDetail";
            }
            model.addAttribute("product", product);
            return "user/productDetail";
        } catch (Exception e) {
            model.addAttribute("error", "Error retrieving product details: " + e.getMessage());
            return "user/productDetail";
        }
    }

    @GetMapping("/main")
    public String viewProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products available nahi mila.");
        }
        model.addAttribute("products", products);

        return "user/main"; // user/main.html
    }

    @GetMapping("/products/view/{productId}")
    public String viewProductDetails(@PathVariable Long productId, Model model) {
        try {
            Product product = productService.getProductById(productId);
            if (product == null) {
                model.addAttribute("error", "Product not found");
                return "user/productDetail";
            }
            // Get reviews for the product
            List<Review> reviews = reviewService.getReviewsByProductId(productId);

            model.addAttribute("product", product);
            model.addAttribute("reviews", reviews);

            // Get related products
            List<Product> relatedProducts = productService.getRelatedProducts(product);
            model.addAttribute("relatedProducts", relatedProducts);

            return "user/productDetail";
        } catch (Exception e) {
            model.addAttribute("error", "Error retrieving product details: " + e.getMessage());
            return "user/productDetail";
        }
    }

    @GetMapping("/orders")
    public String orders() {
        return "user/orderHistory";
    }

    @GetMapping("/checkout")
    public String checkout(
            Model model) {

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
        if (user == null) {
            return "redirect:/login";
        }

        Cart cart = cartService.getCartByUser(user);
        model.addAttribute("cart", cart);
        return "user/checkout";
    }

    @GetMapping("/history")
    public String orderHistory() {
        return "user/history";
    }

    @GetMapping("/payment")
    public String payment() {
        return "user/payment";
    }

    @GetMapping("/ordersummary")
    public String ordersummary(Model model) {
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
        if (user == null) {
            return "redirect:/login";
        }
        List<Orders> userOrders = orderService.getAllOrders(user.getId());
       
        model.addAttribute("orders", userOrders);
        return "user/ordersummary";
    }

    @GetMapping("/orders/{id}")
    public String orderDetails(@PathVariable Long id, Model model) {
        System.out.println("Fetching delivered orders for order ID: " + id);
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
        System.out.println("Logged in user email: " + email);
        User user = userRepository.findByEmail(email);
        if (user == null)
            return "redirect:/login";

      
        Orders deliverOrder = orderService.findOrderById(id);
        Seller seller = sellerService.getSellerById(deliverOrder.getSellerId());

 
        model.addAttribute("deliverOrder", deliverOrder);
        model.addAttribute("seller", seller);
        return "user/userDeliveredOrders";
    }

    @GetMapping("/orders/cancel/{id}")
    public String cancelOrder(Model model, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        

       boolean status = orderService.getOrdersById(id);
       if(status){
              redirectAttributes.addFlashAttribute("alertType", "success");
                redirectAttributes.addFlashAttribute("alertTitle", "Order Cancellation Successful");
                redirectAttributes.addFlashAttribute("alertMessage", "Your order has been successfully cancelled.");
       }else{
              redirectAttributes.addFlashAttribute("alertType", "error");
                redirectAttributes.addFlashAttribute("alertTitle", "Order Cancellation Failed");        
                redirectAttributes.addFlashAttribute("alertMessage", "Unable to cancel the order. Please contact customer support.");
       }
       return "redirect:/user/ordersummary";

    }
}