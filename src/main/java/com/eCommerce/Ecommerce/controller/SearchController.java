package com.eCommerce.Ecommerce.controller;

import com.eCommerce.Ecommerce.dto.SearchCriteria;
import com.eCommerce.Ecommerce.Entities.Product;
import com.eCommerce.Ecommerce.Services.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
@RequiredArgsConstructor
public class SearchController {

    @Autowired
    private ProductSearchService searchService;

    @GetMapping("/search")
    public String searchProducts(@ModelAttribute SearchCriteria criteria, Model model) {
        Page<Product> products = searchService.searchProducts(criteria);

        model.addAttribute("products", products);
        model.addAttribute("criteria", criteria);

        return "user/search-results";
    }
}