package com.eCommerce.Ecommerce.Form;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserForm {

   
    private String name;
    private String phoneNumber;
    private String email;
    private String address;
    private String city;
    private String state;
    private int pinCode;
    private String password;
    private String confirmPassword;
    private MultipartFile profilePic;
    private String role;
    
   
   
 
 
    
}
