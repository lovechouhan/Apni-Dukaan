package com.eCommerce.Ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.eCommerce.Ecommerce.Entities.Address;
import com.eCommerce.Ecommerce.Entities.Provider;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "Please provide a valid 10-digit phone number")
    private String phoneNumber;

    private String profilePic;

    // Addresses associated with the user
    private String role; // e.g., "USER", "ADMIN"
    private Provider provider; // e.g., OAuth2 provider ID
    private LocalDateTime createdAt;
    private Set<Address> addresses = new HashSet<>();
    // Provider info (only for OAuth2 users)

    // Account status
    private boolean isEmailVerified;
    private boolean enabled;
}