package com.eCommerce.Ecommerce.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProduct {

    private String title;
    private String description;
    private double price;
    private int discountedPrice;
    private int discountedPercent;
    private int stockQuantity;
    private String brand;
    private String categoryName;
    private String color;
    private String imgURL;
    

   
   
}
