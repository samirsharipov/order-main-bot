package com.example.mainbot.repository;

import com.example.mainbot.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByShopId(Long shopId);
}
