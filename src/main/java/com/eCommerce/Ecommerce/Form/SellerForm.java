package com.eCommerce.Ecommerce.Form;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import com.eCommerce.Ecommerce.Domain.UserRoles;
import com.eCommerce.Ecommerce.Entities.Address;
import com.eCommerce.Ecommerce.Entities.BankDetails;
import com.eCommerce.Ecommerce.Entities.BussinessDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SellerForm {

    private String SellerName;
    private String email;
    private String password;
    private MultipartFile profilePic;
    private String profileImageUrl;
    private String phoneNumber;

    @Embedded
    private BussinessDetails bussinessDetails = new BussinessDetails();

    @Embedded
    private BankDetails bankDetails = new BankDetails();

    @OneToOne(cascade = CascadeType.ALL)
    private Address pickupAddress = new Address();

    private String GSTIN;

    private String role = UserRoles.SELLER;

}
