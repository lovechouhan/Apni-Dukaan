package com.eCommerce.Ecommerce.Entities.DTO;
import java.util.List;
public class ProductDTO {
    public String title;
    public String description;
    public int MRPprice;
    public int sellingPrice;
    public int quantity;
    public String color;
    public String brand;
    public int discountPercentage;
    public Long sellerId;
    public List<String> images;
    public List<String> sizes;
    public Long categoryId;
}

