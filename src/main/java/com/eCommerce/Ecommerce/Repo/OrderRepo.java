package com.eCommerce.Ecommerce.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eCommerce.Ecommerce.Entities.Orders;

@Repository
public interface OrderRepo extends JpaRepository<Orders, Long> {

    @Query("SELECT o FROM Orders o WHERE o.user.id = ?1")
    List<Orders> findAllByUserId(Long userId);

    @Query("SELECT COUNT(o) FROM Orders o WHERE o.sellerId = ?1")
    int countBySellerId(Long currentSellerId);

    @Query("SELECT o FROM Orders o WHERE o.sellerId = ?1")
    List<Orders> findAllOrdersBySellerId(Long currentSellerId);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0.0) FROM Orders o WHERE o.sellerId = ?1")
    int sumTotalAmountBySellerId(Long currentSellerId);

    @Query("SELECT COUNT(o) FROM OrderItem o WHERE o.userID = ?1")
    int countProductsByUserId(Long id);

    @Query("SELECT COALESCE(SUM((o.totalAmount) + o.deliveryCharge), 0.0) FROM Orders o WHERE o.user.id = ?1")
    int sumTotalSpendingsByUserId(Long id);

  

}