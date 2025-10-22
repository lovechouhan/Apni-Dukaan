package com.eCommerce.Ecommerce.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.eCommerce.Ecommerce.Entities.Cart;
import com.eCommerce.Ecommerce.Entities.CartItem;
import com.eCommerce.Ecommerce.Entities.OrderItem;
import com.eCommerce.Ecommerce.Entities.OrderStatus;
import com.eCommerce.Ecommerce.Entities.Orders;
import com.eCommerce.Ecommerce.Entities.PaymentStatus;
import com.eCommerce.Ecommerce.Entities.Seller;
import com.eCommerce.Ecommerce.Entities.User;
import com.eCommerce.Ecommerce.Repo.OrderRepo;
import com.eCommerce.Ecommerce.Repo.UserRepo;
import com.razorpay.Order;
import com.eCommerce.Ecommerce.Repo.SellerRepo;

@Service
public class OrderService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserServices userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private SellerRepo sellerRepo;

    public Orders saveOrder(Order razorpayOrder) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email;

        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            OAuth2User oauth2User = oauthToken.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else {
            email = auth.getName();
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found for email: " + email);
        }

        Cart cart = cartService.getCartByUser(user);
        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty, cannot create order.");
        }

        Orders order = new Orders();
        order.setOrderId(UUID.randomUUID().toString()); // Razorpay ID / temp ID
        order.setUser(user);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSellingPrice(cartItem.getSellingPrice());
            orderItem.setMrpPrice(cartItem.getMRPprice());
            orderItem.setSize(cartItem.getSize());
            orderItem.setUserID(user.getId());
            orderItem.setOrder(order); // 🔑 Set parent reference
            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);

        order.setTotalAmount(Double.parseDouble(razorpayOrder.get("amount").toString()) / 100.0);

        if ("created".equalsIgnoreCase((String) razorpayOrder.get("status"))) {
            order.setPaymentStatus(PaymentStatus.COMPLETED);
        } else {
            order.setPaymentStatus(PaymentStatus.PENDING);
        }
        order.setOrderStatus(OrderStatus.SHIPPED);
        order.setTotalMRPPrice(cart.getTotalMRPPrice());
        order.setTotalSellingPrice(cart.getTotalAmount());
        order.setDiscount(cart.getDiscount());
        order.setTotalItems(cart.getTotalItems());
        order.setDeliveryCharge(50.0);

        order.setSellerId(orderItems.get(0).getProduct().getSeller().getId()); // Assuming all items are from the same
                                                                               // seller
        order.setSellerName(orderItems.get(0).getProduct().getSeller().getSellerName());
        Orders savedOrder = orderRepo.save(order);
        cartService.clearCart(user);

        System.out.println("Order saved with ID: " + savedOrder);

        return savedOrder;

    }

    public List<Orders> getAllOrders(Long userId) {
        return orderRepo.findAllByUserId(userId);
    }

    public int getOrderCountForCurrentSeller() {
        // Assuming you have a method to get the currently logged-in seller's ID
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

        Seller seller = sellerRepo.findByEmail(email);
        if (seller == null) {
            throw new RuntimeException("Seller not found");
        }
        Long currentSellerId = seller.getId();
        return orderRepo.countBySellerId(currentSellerId);
    }

    public List<Orders> getAllOrdersForCurrentSeller() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email;
        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            OAuth2User oauth2User = oauthToken.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else {
            email = auth.getName();
        }

        Seller seller = sellerRepo.findByEmail(email);
        if (seller == null) {
            throw new RuntimeException("Seller not found");
        }
        Long currentSellerId = seller.getId();
        return orderRepo.findAllOrdersBySellerId(currentSellerId);
    }

    public Orders findOrderById(Long id) {
        return orderRepo.findById(id).orElse(null);
    }

    public int getTotalEarningsForCurrentSeller() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email;
        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            OAuth2User oauth2User = oauthToken.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else {
            email = auth.getName();
        }

        Seller seller = sellerRepo.findByEmail(email);
        if (seller == null) {
            throw new RuntimeException("Seller not found");
        }
        Long currentSellerId = seller.getId();
        Double sum = (double) orderRepo.sumTotalAmountBySellerId(currentSellerId);
        return (int) Math.round(sum != null ? sum : 0.0);
    }

    public boolean getOrdersById(Long id) {
        Orders order = orderRepo.findById(id).orElse(null);
        if (order != null) {
            order.setOrderStatus(OrderStatus.CANCELLED);
            orderRepo.save(order);
            return true;
        }
        return false;

    }

}