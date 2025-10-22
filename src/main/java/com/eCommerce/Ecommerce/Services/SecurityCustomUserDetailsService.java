
package com.eCommerce.Ecommerce.Services;
import org.springframework.stereotype.Service;

import com.eCommerce.Ecommerce.Repo.UserRepo;


import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.eCommerce.Ecommerce.Entities.Seller;
import com.eCommerce.Ecommerce.Entities.User;
import com.eCommerce.Ecommerce.Repo.SellerRepo;
import com.eCommerce.Ecommerce.Repo.UserRepo;
import com.eCommerce.Ecommerce.Services.UserServices;






@Service
public class SecurityCustomUserDetailsService implements UserDetailsService {

   
    private final UserRepo userRepo;
    private final SellerRepo sellerRepo;
    private final UserServices userServices;

    public SecurityCustomUserDetailsService(UserRepo userRepo, SellerRepo sellerRepo, @Lazy UserServices userServices) {
        this.userRepo = userRepo;
        this.sellerRepo = sellerRepo;
        this.userServices = userServices;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find user by email
        User user = userRepo.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        String email = user.getEmail();
        String role = user.getRole().toString();

        if ("SELLER".equals(role)) {
            Seller seller = sellerRepo.findByEmail(email);
            if (seller == null) {
                throw new UsernameNotFoundException("Seller not found with email: " + email);
            }
            return seller;
        } else {
            return userServices.getUserByEmail(email);
        }
    }
}
