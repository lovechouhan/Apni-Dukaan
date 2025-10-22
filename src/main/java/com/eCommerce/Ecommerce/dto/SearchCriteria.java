package com.eCommerce.Ecommerce.dto;

import lombok.Data;

@Data
public class SearchCriteria {
    private String keyword;
    private String category;
    private Double minPrice;
    private Double maxPrice;
    private String sortBy = "name";
    private String sortDirection = "ASC";
    private Integer pageNumber = 0;
    private Integer pageSize = 12;
}