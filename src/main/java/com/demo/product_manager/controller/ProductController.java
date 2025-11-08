package com.demo.product_manager.controller;

import com.demo.product_manager.dto.ProductRequest;
import com.demo.product_manager.entity.Product;
import com.demo.product_manager.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/products")
    @ResponseBody
    public String getProducts() {
        List<Product> products = productService.getAllProducts();
        return buildProductTableHtml(products);
    }

    @PostMapping("/products")
    @ResponseBody
    public String addProduct(@ModelAttribute ProductRequest request) {
        Product product = new Product();
        product.setTitle(request.getTitle());
        product.setVendor(request.getVendor());
        product.setProductType(request.getProductType());
        product.setPrice(request.getPrice());

        productService.addProduct(product);

        List<Product> products = productService.getAllProducts();
        return buildProductTableHtml(products);
    }

    private String buildProductTableHtml(List<Product> products) {
        StringBuilder html = new StringBuilder();
        html.append("<table class=\"table\">");
        html.append("<thead><tr>");
        html.append("<th>ID</th>");
        html.append("<th>Title</th>");
        html.append("<th>Vendor</th>");
        html.append("<th>Product Type</th>");
        html.append("<th>Price</th>");
        html.append("</tr></thead>");
        html.append("<tbody>");

        for (Product product : products) {
            html.append("<tr>");
            html.append("<td>").append(product.getId()).append("</td>");
            html.append("<td>").append(escapeHtml(product.getTitle())).append("</td>");
            html.append("<td>").append(escapeHtml(product.getVendor())).append("</td>");
            html.append("<td>").append(escapeHtml(product.getProductType())).append("</td>");
            html.append("<td>$").append(product.getPrice()).append("</td>");
            html.append("</tr>");
        }

        html.append("</tbody></table>");
        return html.toString();
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}