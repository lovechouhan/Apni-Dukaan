package com.eCommerce.Ecommerce.controller;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eCommerce.Ecommerce.Entities.Cart;
import com.eCommerce.Ecommerce.Entities.CartItem;
import com.eCommerce.Ecommerce.Entities.Product;
import com.eCommerce.Ecommerce.Entities.User;
import com.eCommerce.Ecommerce.Repo.CartItemRepo;
import com.eCommerce.Ecommerce.Services.CartService;
import com.eCommerce.Ecommerce.Services.ProductService;

// @{/cart/decreaseQuantity/{id}(id=${item.id})}
@Controller
@RequestMapping("/user/cart")
public class CartController {

    @Autowired
    private ProductService productService;

    @Autowired
    private com.eCommerce.Ecommerce.Repo.UserRepo userRepository;

    @Autowired
    private com.eCommerce.Ecommerce.Repo.CartRepo cartRepo;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartItemRepo cartItemRepo;

    // View cart
    @GetMapping("/{productId}")
    public String cart(@PathVariable("productId") Long productId, Model model) {
        // 🔹 1. Product fetch
        Product product = productService.getProductById(productId);
        model.addAttribute("product", product);
        // 🔹 2. Logged-in user fetch
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

        // 🔹 3. Cart me product add karo
        cartService.addToCart(user, product);

        // 🔹 4. User ka cart fetch karo
        Cart cart = cartService.getCartByUser(user);
        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);
        // 🔹 5. Model me cart + cartItems bhejo
        double totalAmount = cart.getTotalAmount();
               
        model.addAttribute("cart", cart);
        // model.addAttribute("cartItems", cart.getItems());
        model.addAttribute("cartItems", cartItem);
        model.addAttribute("totalAmount", totalAmount);

        return "user/cart";
    }

    @GetMapping("")
    public String viewCart(Model model) {
        // Get logged in user using SecurityContext
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
        System.out.println("Logged in user email ka mila huaa email : " + email);

        User user = userRepository.findByEmail(email);

        // Check if user is logged in
        if (user == null) {
            return "redirect:/login";
        }

        System.out.println("Logged in user email: " + user.getEmail());

        // Get cart
        Cart cart = cartService.getCartByUser(user);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
        }

        // Add cart to model
        model.addAttribute("cart", cart);
        model.addAttribute("cartItems", cart.getCartItems() != null ? cart.getCartItems() : new HashSet<>());

        return "user/cart.html";
    }

    // Add to cart
    @GetMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId) {
        // 1️⃣ Logged-in user fetch
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

        // 2️⃣ Add product to cart
        Product product = productService.getProductById(productId);
        if (product != null) {
            cartService.addToCart(user, product);
        }

        return "redirect:/user/cart";
    }

    // for removing item from cart
    @GetMapping("/remove/{id}")
    public String removeItem(@PathVariable("id") Long cartItemId) {
        // Get logged in user
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
        if (user == null)
            return "redirect:/login";

        // Find the cart item
        CartItem cartItem = cartItemRepo.findById(cartItemId).orElse(null);

        Cart cart = cartService.getCartByUser(user);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
        }
        if (cartItem != null && cartItem.getUserId().equals(user.getId())) {
            // Remove the item
            cart.getCartItems().remove(cartItem);
            // update total items and total MRP price
            cart.setTotalItems(cart.getCartItems().size());
            cart.setTotalMRPPrice((int) cart.getCartItems().stream()
                    .mapToDouble(item -> item.getMRPprice() * item.getQuantity())
                    .sum());

            // update total amount and discount
            double totalAmount = cart.getCartItems().stream()
                    .mapToDouble(item -> item.getSellingPrice() * item.getQuantity())
                    .sum();
            cart.setTotalAmount(totalAmount);
            cart.setDiscount(cart.getTotalMRPPrice() - (int) totalAmount);

          

            Cart Usercart = cartService.getCartByUser(user);
            Long cartId = Usercart.getId(); // This gets the cart ID

            CartItem UsercartItem = cartItemRepo.findById(cartItemId).orElse(null);
            Long cartitemskiId = UsercartItem.getId();

            cartService.updateCart(cartId, cartitemskiId, cart.getTotalItems());

            
        }

        return "redirect:/user/cart";
    }

    @GetMapping("/decreaseQuantity/{id}")
    public String decreaseQuantity(@PathVariable("id") Long cartItemId) {
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
        if (user == null)
            return "redirect:/login";

        // Find the cart item
        CartItem cartItem = cartItemRepo.findById(cartItemId).orElse(null);
        if (cartItem == null) {
            System.out.println("Cart item not found with ID: " + cartItemId);
            return "redirect:/cart";
        }

        Cart cart = cartService.getCartByUser(user);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
        }

        cartItem.setQuantity(cartItem.getQuantity() - 1);
        
        if (cartItem.getQuantity() <= 0) {
            cart.getCartItems().remove(cartItem);
            cartItemRepo.delete(cartItem);
        } else {
            cartItemRepo.save(cartItem);
        }

        cart.setTotalItems(cart.getCartItems().size());
        cart.setTotalMRPPrice((int) cart.getCartItems().stream()
                .mapToDouble(item -> item.getMRPprice() * item.getQuantity())
                .sum());
        double totalAmount = cart.getCartItems().stream()
                .mapToDouble(item -> item.getSellingPrice() * item.getQuantity())
                .sum();
        cart.setTotalAmount(totalAmount);
        cart.setDiscount(cart.getTotalMRPPrice() - (int) totalAmount);
        cartItem.setDiscountPrice(cartItem.getMRPprice() - cartItem.getSellingPrice());
        cartRepo.save(cart);

        return "redirect:/user/cart";

    }

    @GetMapping("/increaseQuantity/{id}")
    public String increaseQuantity(@PathVariable("id") Long cartItemId) {
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
        if (user == null)
            return "redirect:/login";

        // Find the cart item
        CartItem cartItem = cartItemRepo.findById(cartItemId).orElse(null);
        if (cartItem == null) {
            System.out.println("Cart item not found with ID: " + cartItemId);
            return "redirect:/cart";
        }

        Cart cart = cartService.getCartByUser(user);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
        }

        cartItem.setQuantity(cartItem.getQuantity() + 1);

        if (cartItem.getQuantity() <= 0) {
            cart.getCartItems().remove(cartItem);
            cartItemRepo.delete(cartItem);
        } else {
            cartItemRepo.save(cartItem);
        }

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

        return "redirect:/user/cart";
    }
}
