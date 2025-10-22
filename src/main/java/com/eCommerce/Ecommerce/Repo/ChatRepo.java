package com.eCommerce.Ecommerce.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.eCommerce.Ecommerce.Entities.ChatEntity;

@Repository
public interface ChatRepo extends JpaRepository<ChatEntity, Long> {
    List<ChatEntity> findTop10ByUserIdOrderByTimestampDesc(Long userId);
}
