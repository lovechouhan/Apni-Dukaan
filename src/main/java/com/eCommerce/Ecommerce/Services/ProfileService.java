package com.eCommerce.Ecommerce.Services;


import com.eCommerce.Ecommerce.Entities.User;

import com.eCommerce.Ecommerce.Entities.Seller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import com.eCommerce.Ecommerce.Repo.SellerRepo;
import com.eCommerce.Ecommerce.Repo.UserRepo;
import com.eCommerce.Ecommerce.dto.SellerProfileDTO;
import com.eCommerce.Ecommerce.dto.UserProfileDTO;

@Service
public class ProfileService {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private SellerRepo sellerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Path rootLocation = Paths.get("uploads");

    // User Profile Methods
    public UserProfileDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setName(user.getName());
        profileDTO.setEmail(user.getEmail());
        profileDTO.setPhoneNumber(user.getPhoneNumber());
      

        return profileDTO;
    }

    public void updateUserProfile(Long userId, UserProfileDTO profileDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(profileDTO.getName());
        user.setEmail(profileDTO.getEmail());
        user.setPhoneNumber(profileDTO.getPhoneNumber());
        // ;

        userRepository.save(user);
    }

    public void updateUserPassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public String uploadUserProfilePicture(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), rootLocation.resolve(filename));

        // user.setProfilePicture(filename);
        userRepository.save(user);

        return filename;
    }

    // Seller Profile Methods
    public SellerProfileDTO getSellerProfile(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        SellerProfileDTO profileDTO = new SellerProfileDTO();
        profileDTO.setSellerName(seller.getSellerName());
        profileDTO.setEmail(seller.getEmail());
        profileDTO.setPhoneNumber(seller.getPhoneNumber());
        profileDTO.getBussinessDetails().setBusinessName(seller.getBussinessDetails().getBusinessName());
        profileDTO.getBussinessDetails().setBusinessAddress(seller.getBussinessDetails().getBusinessAddress());
        profileDTO.getBussinessDetails().setBusinessEmail(seller.getBussinessDetails().getBusinessEmail());
        profileDTO.getBankDetails().setAccountNumber(seller.getBankDetails().getAccountNumber());
        profileDTO.getBankDetails().setIfscCode(seller.getBankDetails().getIfscCode());
        profileDTO.getBankDetails().setAccountHolderName(seller.getBankDetails().getAccountHolderName());
        profileDTO.getPickupAddress().setStreetAddress((seller.getPickupAddress().getStreetAddress()));
        profileDTO.getPickupAddress().setCity(seller.getPickupAddress().getCity());
        profileDTO.getPickupAddress().setState(seller.getPickupAddress().getState());
        profileDTO.getPickupAddress().setZipCode(seller.getPickupAddress().getZipCode());
        profileDTO.setGstin(seller.getGSTIN());
        profileDTO.setRole(seller.getRole());
     
      

        return profileDTO;
    }

    public void updateSellerProfile(Long sellerId, SellerProfileDTO profileDTO) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        seller.setSellerName(profileDTO.getSellerName());
        seller.setEmail(profileDTO.getEmail());
        seller.setPhoneNumber(profileDTO.getPhoneNumber());
        seller.getBussinessDetails().setBusinessName(profileDTO.getBussinessDetails().getBusinessName());
        seller.getBussinessDetails().setBusinessAddress(profileDTO.getBussinessDetails().getBusinessAddress());
        seller.getBussinessDetails().setBusinessEmail(profileDTO.getBussinessDetails().getBusinessEmail());
        seller.getBankDetails().setAccountNumber(profileDTO.getBankDetails().getAccountNumber());
        seller.getBankDetails().setIfscCode(profileDTO.getBankDetails().getIfscCode());
        seller.getBankDetails().setAccountHolderName(profileDTO.getBankDetails().getAccountHolderName());
        seller.getPickupAddress().setStreetAddress(profileDTO.getPickupAddress().getStreetAddress());
        seller.getPickupAddress().setCity(profileDTO.getPickupAddress().getCity());
        seller.getPickupAddress().setState(profileDTO.getPickupAddress().getState());
        seller.getPickupAddress().setZipCode(profileDTO.getPickupAddress().getZipCode());
        seller.setGSTIN(profileDTO.getGstin());
        sellerRepository.save(seller);
    }

    public void updateSellerPassword(Long sellerId, String oldPassword, String newPassword) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        if (!passwordEncoder.matches(oldPassword, seller.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        seller.setPassword(passwordEncoder.encode(newPassword));
        sellerRepository.save(seller);
    }

    public String uploadSellerStoreLogo(Long sellerId, MultipartFile file) throws IOException {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), rootLocation.resolve(filename));

        // seller.setStoreLogo(filename);
        sellerRepository.save(seller);

        return filename;
    }
}