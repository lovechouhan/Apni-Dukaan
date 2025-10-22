package com.eCommerce.Ecommerce.dto;

import java.time.LocalDateTime;

import com.eCommerce.Ecommerce.Domain.AccountStatus;
import com.eCommerce.Ecommerce.Domain.UserRoles;
import com.eCommerce.Ecommerce.Entities.Address;
import com.eCommerce.Ecommerce.Entities.BankDetails;
import com.eCommerce.Ecommerce.Entities.BussinessDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellerSettingsDTO {
 

    private String SellerName;
    private String email;
    private String password;
    private String phoneNumber;
    private LocalDateTime createdAt = LocalDateTime.now();
 
    private BussinessDetails bussinessDetails = new BussinessDetails();

    
    private BankDetails bankDetails = new BankDetails();

    @OneToOne(cascade = CascadeType.ALL)
    private Address pickupAddress = new Address();

 

    private String role = UserRoles.SELLER;


    private boolean isemailVerified = false;
    private boolean enabled = false;

    private AccountStatus accountStatus = AccountStatus.PENDING_VERIFICATION;   // Require signature on delivery


}