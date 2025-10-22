package com.eCommerce.Ecommerce.Services;

import org.springframework.stereotype.Service;

import com.eCommerce.Ecommerce.Entities.Seller;
import com.eCommerce.Ecommerce.dto.SellerSettingsDTO;
import com.eCommerce.Ecommerce.Repo.SellerRepo;
import com.eCommerce.Ecommerce.Repo.UserRepo;

import org.springframework.beans.factory.annotation.Autowired;


@Service
public class SellerSettingsService {

    // Get seller settings
    @Autowired
    private SellerRepo sellerRepository;
    @Autowired
    private UserRepo userRepository;
    
    public SellerSettingsDTO getSellerSettings(String email) {
        Seller seller = sellerRepository.findByEmail(email);
        if (seller == null) {
            throw new IllegalArgumentException("Seller not found");
        }
        SellerSettingsDTO settings = new SellerSettingsDTO();
       settings.setSellerName(seller.getSellerName());
        settings.setEmail(seller.getEmail());
        settings.setPhoneNumber(seller.getPhoneNumber());
        settings.setEnabled(seller.isEnabled());
        settings.setRole(seller.getRole());
        settings.setCreatedAt(seller.getCreatedAt());
        settings.setBussinessDetails(seller.getBussinessDetails());
        settings.setBankDetails(seller.getBankDetails());
        settings.setPickupAddress(seller.getPickupAddress());
        settings.setAccountStatus(seller.getAccountStatus());
        settings.setIsemailVerified(seller.isIsemailVerified());
        settings.setPassword(seller.getPassword());
        return settings;
    }
       
    public Seller getSellerByEmail(String email) {
        return sellerRepository.findByEmail(email);
    }

    public SellerSettingsDTO updateSellerSettings(String email, SellerSettingsDTO settings) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateSellerSettings'");
    }

}