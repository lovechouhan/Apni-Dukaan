package com.eCommerce.Ecommerce.Services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.eCommerce.Ecommerce.Repo.SellerRepo;
import com.eCommerce.Ecommerce.Repo.UserRepo;
import com.eCommerce.Ecommerce.dto.SellerSettingsDTO;
import com.eCommerce.Ecommerce.dto.UserSettingsDTO;
import com.eCommerce.Ecommerce.Entities.Seller;
import com.eCommerce.Ecommerce.Entities.User;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserSettingsService {

    @Autowired
    private UserRepo userRepository;
    @Autowired
    private SellerRepo sellerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Get user settings
    public UserSettingsDTO getUserSettings(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        UserSettingsDTO settings = new UserSettingsDTO();
        settings.setName(user.getName());
        settings.setEmail(user.getEmail());
        settings.setPhoneNumber(user.getPhoneNumber());
        settings.setEnabled(user.isEnabled());
        settings.setProfilePic(user.getProfileImageUrl());
        settings.setRole(user.getRole());
        settings.setAddresses(user.getAddresses());
        settings.setProvider(user.getProvider());
        return settings;
        
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    // Update user settings
    public boolean updateUserSettings(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.save(user);
        return true;
    }

    public boolean changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false; // Current password does not match
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }
}