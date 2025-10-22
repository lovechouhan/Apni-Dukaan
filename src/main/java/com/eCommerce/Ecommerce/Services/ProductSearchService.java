package com.eCommerce.Ecommerce.Services;

import com.eCommerce.Ecommerce.dto.SearchCriteria;
import com.eCommerce.Ecommerce.Entities.Product;
import com.eCommerce.Ecommerce.Repo.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    @Autowired
    private ProductRepo productRepo;

    public Page<Product> searchProducts(SearchCriteria criteria) {
        Sort sort = Sort.by(
                Sort.Direction.fromString(criteria.getSortDirection()),
                criteria.getSortBy());

        PageRequest pageRequest = PageRequest.of(
                criteria.getPageNumber(),
                criteria.getPageSize(),
                sort);

        return productRepo.searchProducts(
                criteria.getKeyword(),
                criteria.getCategory(),
                criteria.getMinPrice(),
                criteria.getMaxPrice(),
                pageRequest);
    }
}