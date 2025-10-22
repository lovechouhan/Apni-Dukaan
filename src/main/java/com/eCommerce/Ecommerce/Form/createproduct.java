package com.eCommerce.Ecommerce.Form;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class createproduct {

    private String name;
    private String description;
    private int MRPprice;
    private int sellingPrice;
    private int quantity;
    private String color;
    private String brand;
    private int discountPrice;
    private String category; // Changed from categoryId to category
    private Long sellerId;
    private MultipartFile images; // Single image file
    // Getters and Setters

}
