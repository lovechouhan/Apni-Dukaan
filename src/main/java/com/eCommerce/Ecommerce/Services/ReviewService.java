package com.eCommerce.Ecommerce.Services;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.eCommerce.Ecommerce.Repo.ReviewRepo;
import com.eCommerce.Ecommerce.Entities.Review;
import com.eCommerce.Ecommerce.Entities.User;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepo reviewRepo;

    @Autowired
    private ProductService productService;

    public void saveReview(String review, int rating, Long productId, User user) {
        Review reviewEntity = new Review();
        reviewEntity.setReviewText(review);
        reviewEntity.setRating(rating);
        reviewEntity.setProduct(productService.getProductById(productId));
        reviewEntity.setUser(user);
        reviewEntity.setUserName(user.getName());
        reviewRepo.save(reviewEntity);
    }

    public List<Review> getReviewsByProductId(Long productId) {
        return reviewRepo.findByProductId(productId);
    }
}