package com.eCommerce.Ecommerce.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.eCommerce.Ecommerce.Entities.Address;

import com.eCommerce.Ecommerce.Entities.Provider;


import jakarta.persistence.CascadeType;

import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingsDTO {
    // Notification Preferences
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String profilePic;
    private boolean enabled;
    private String role ; 
    private Provider provider = Provider.SELF;

    private String providerId;
    private LocalDateTime createdAt = LocalDateTime.now();


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Address> addresses = new HashSet<>();
  

    
}