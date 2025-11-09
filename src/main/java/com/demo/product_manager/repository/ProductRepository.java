package com.demo.product_manager.repository;

import com.demo.product_manager.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByTitleContainingIgnoreCaseOrderByPriceAsc(String title);
    List<Product> findAllByOrderByPriceAsc();
}