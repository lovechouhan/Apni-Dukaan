package com.eCommerce.Ecommerce.dto;

import com.eCommerce.Ecommerce.Domain.UserRoles;
import com.eCommerce.Ecommerce.Entities.Address;
import com.eCommerce.Ecommerce.Entities.BankDetails;
import com.eCommerce.Ecommerce.Entities.BussinessDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SellerProfileDTO {
    // Basic Info
    @NotBlank(message = "Seller name is required")
    private String sellerName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "Please provide a valid 10-digit phone number")
    private String phoneNumber;

    // Business Details
    // @NotBlank(message = "GSTIN is required")
    // @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$", message = "Please provide a valid GSTIN")
    private String gstin;

    @Embedded
    private BussinessDetails bussinessDetails = new BussinessDetails();

    @Embedded
    private BankDetails bankDetails = new BankDetails();

    @OneToOne(cascade = CascadeType.ALL)
    private Address pickupAddress = new Address();

   
    private String role = UserRoles.SELLER;

    // Business Address
    

    // Bank Details
    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotBlank(message = "IFSC code is required")
    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Please provide a valid IFSC code")
    private String ifscCode;

    @NotBlank(message = "Account holder name is required")
    private String accountHolderName;

    // Status
    private boolean emailVerified;
    private boolean enabled;
    private String accountStatus; // e.g., "ACTIVE", "SUSPENDED", "PENDING_VERIFICATION"

    // Metadata
    private String logo; // Store/Business logo
    private String description; // Business description
}