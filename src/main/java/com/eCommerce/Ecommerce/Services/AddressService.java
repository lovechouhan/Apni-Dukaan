package com.eCommerce.Ecommerce.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eCommerce.Ecommerce.Entities.Address;
import com.eCommerce.Ecommerce.Repo.AddressRepo;

@Service
public class AddressService {

    @Autowired
    private AddressRepo addressRepository;

    public void saveAddress(String fullName, String streetAddress, String city, String email,String state,
                             String zipCode, String mobile) {
        // Logic to save the address details
        // This could involve saving to a database or session
        System.out.println("Address saved: " + fullName + ", " + streetAddress + ", " + city + ", " + email + ", " + state + ", " + zipCode + ", " + mobile);

   
        Address address = new Address();
        address.setFullName(fullName);
        address.setStreetAddress(streetAddress);
        address.setCity(city);
        address.setEmail(email);
        address.setState(state);
        address.setZipCode(zipCode);
        address.setMobile(mobile);
        
        //save to database (to be implemented)
         addressRepository.save(address);

    }
}


    

