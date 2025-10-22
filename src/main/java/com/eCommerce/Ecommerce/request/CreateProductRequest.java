package com.eCommerce.Ecommerce.request;

import java.util.List;

import lombok.Data;

@Data
public class CreateProductRequest {
    private String name;
    private String description;
    private int mrpPrice;
    private int sellingPrice;
    private String color;
 
    private String category;
    private String brand;
    private int quantity;
    private int discountPrice;
    private String images; 
    private String sizes;  // Comma-separated sizes
}
