package com.demo.product_manager.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {
    private String title;
    private String vendor;
    private String productType;
    private BigDecimal price;
}