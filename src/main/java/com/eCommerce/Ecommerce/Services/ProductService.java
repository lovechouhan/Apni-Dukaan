package com.eCommerce.Ecommerce.Services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.eCommerce.Ecommerce.Entities.Product;
import com.eCommerce.Ecommerce.Entities.Seller;
import com.eCommerce.Ecommerce.Repo.ProductRepo;
import com.eCommerce.Ecommerce.Repo.SellerRepo;
import com.eCommerce.Ecommerce.request.CreateProductRequest;

@Service
public class ProductService {
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private SellerService sellerService;
    @Autowired
    private SellerRepo sellerRepo;


    public long getTotalProducts() {
        return productRepo.count();
    }

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepo.findByCategory(category);
    }

    public Product createProduct(CreateProductRequest request, Seller seller) {
       
        int discountPercentage = calculateDiscountPercentage(request.getMrpPrice(), request.getSellingPrice());

        Product product = new Product();
        product.setSeller(seller);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setMRPprice(request.getMrpPrice());
        product.setQuantity(request.getQuantity());
        product.setImages(request.getImages());
        product.setBrand(request.getBrand());
        // product.setCategory(category);
        product.setCategory(request.getCategory());
        product.setCreatedAt(LocalDateTime.now());
        product.setColor(request.getColor());
        product.setSellerName(seller.getSellerName());
        // Assuming sizes are comma-separated in the String
        String sizesStr = request.getSizes();
        List<String> sizesList = sizesStr != null ? java.util.Arrays.asList(sizesStr.split(","))
                : java.util.Collections.emptyList();
        product.setSizes(sizesList);
        product.setSellingPrice(request.getSellingPrice());
        product.setDiscountPrice(discountPercentage);

        return productRepo.save(product);
    }

    private int calculateDiscountPercentage(int mrpPrice, int sellingPrice) {
        if (mrpPrice <= 0) {
            throw new IllegalArgumentException("MRP price must be greater than zero");
        }

        double discount = mrpPrice - sellingPrice;
        double discountPercentage = (discount / mrpPrice) * 100;
        return (int) Math.round(discountPercentage);
    }

    public List<Product> getRelatedProducts(Product product) {
        // Get products in the same category, excluding the current product
        List<Product> relatedProducts = productRepo.findByCategoryAndIdNot(
                product.getCategory(),
                product.getId());

        // Limit to 4 related products
        return relatedProducts.size() > 4 ? relatedProducts.subList(0, 4) : relatedProducts;
    }

    public void deleteProduct(Long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        try {
            // Clear the collections before deleting to avoid foreign key constraint issues
            product.getSizes().clear();
            product.setImages(null);
            product.getRatings().clear();
            product.getReviews().clear();

            // Save the changes first
            productRepo.save(product);

            // Then delete the product
            productRepo.delete(product);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting product: " + e.getMessage());
        }
    }

    public Product updateProduct(Long productId, Product product) {
        getProductById(productId); // Ensure product exists
        product.setId(productId);
        return productRepo.save(product);
    }

    public Product getProductById(Long productId) {
        return productRepo.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<Product> searchProducts(String name, String category, Double minPrice, Double maxPrice,
            org.springframework.data.domain.Pageable pageable) {
        return productRepo.searchProducts(name, category, minPrice, maxPrice, pageable).getContent();
    }

    public List<Product> getProductsBySellerId(Long sellerId) {
        return productRepo.findBySellerId(sellerId);
    }

    public int getProductCountForCurrentSeller() {
        // Assuming you have a method to get the currently logged-in seller's ID
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email;
        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            // For OAuth2 (Google) login
            OAuth2User oauth2User = oauthToken.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else {
            // For regular login
            email = auth.getName();
        }

        Seller seller = sellerRepo.findByEmail(email);
        if (seller == null) {
            throw new RuntimeException("Seller not found");
        }
        Long currentSellerId = seller.getId();
        return productRepo.findBySellerId(currentSellerId).size();
    }

    

}