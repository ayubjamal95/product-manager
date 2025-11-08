package com.demo.product_manager.service;

import com.demo.product_manager.entity.Product;
import com.demo.product_manager.repository.ProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String PRODUCTS_API_URL = "https://famme.no/products.json";
    private static final int MAX_PRODUCTS = 50;

    @Scheduled(initialDelay = 0, fixedDelay = Long.MAX_VALUE)
    @Transactional
    public void fetchAndSaveProducts() {
        log.info("Starting scheduled job to fetch products from {}", PRODUCTS_API_URL);

        try {
            // Check if products already exist
            long count = productRepository.count();
            if (count > 0) {
                log.info("Products already exist in database. Skipping fetch.");
                return;
            }

            String jsonResponse = restTemplate.getForObject(PRODUCTS_API_URL, String.class);
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode productsArray = rootNode.get("products");

            if (productsArray != null && productsArray.isArray()) {
                int savedCount = 0;
                for (JsonNode productNode : productsArray) {
                    if (savedCount >= MAX_PRODUCTS) {
                        break;
                    }

                    Product product = new Product();
                    product.setTitle(productNode.get("title").asText());
                    product.setVendor(productNode.get("vendor").asText());
                    product.setProductType(productNode.get("product_type").asText());

                    // Get price from first variant if available
                    JsonNode variantsNode = productNode.get("variants");
                    if (variantsNode != null && variantsNode.isArray() && variantsNode.size() > 0) {
                        String priceStr = variantsNode.get(0).get("price").asText();
                        product.setPrice(new BigDecimal(priceStr));

                        // Store variants as JSONB
                        List<Map<String, Object>> variants = new ArrayList<>();
                        for (JsonNode variantNode : variantsNode) {
                            Map<String, Object> variant = new HashMap<>();
                            variant.put("title", variantNode.get("title").asText());
                            variant.put("price", variantNode.get("price").asText());
                            if (variantNode.has("sku")) {
                                variant.put("sku", variantNode.get("sku").asText());
                            }
                            variants.add(variant);
                        }
                        product.setVariants(variants);
                    }

                    productRepository.save(product);
                    savedCount++;
                }
                log.info("Successfully saved {} products to database", savedCount);
            }
        } catch (Exception e) {
            log.error("Error fetching products from API", e);
        }
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }
}