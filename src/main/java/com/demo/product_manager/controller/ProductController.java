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

    @GetMapping("/search")
    public String searchPage() {
        return "search";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductPage(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/";
        }
        model.addAttribute("product", product);
        return "edit";
    }

    @PostMapping("/products/edit/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute ProductRequest request) {
        Product product = new Product();
        product.setTitle(request.getTitle());
        product.setVendor(request.getVendor());
        product.setProductType(request.getProductType());
        product.setPrice(request.getPrice());

        productService.updateProduct(id, product);
        return "redirect:/";
    }

    @GetMapping("/products")
    @ResponseBody
    public String getProducts() {
        List<Product> products = productService.getAllProducts();
        return buildProductTableHtml(products);
    }

    @GetMapping("/products/search")
    @ResponseBody
    public String searchProducts(@RequestParam(required = false, defaultValue = "") String query) {
        List<Product> products = productService.searchProducts(query);
        return buildSearchResultsHtml(products, query);
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

    @DeleteMapping("/products/{id}")
    @ResponseBody
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
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
        html.append("<th>Actions</th>");
        html.append("</tr></thead>");
        html.append("<tbody>");

        for (Product product : products) {
            html.append("<tr>");
            html.append("<td>").append(product.getId()).append("</td>");
            html.append("<td>").append(escapeHtml(product.getTitle())).append("</td>");
            html.append("<td>").append(escapeHtml(product.getVendor())).append("</td>");
            html.append("<td>").append(escapeHtml(product.getProductType())).append("</td>");
            html.append("<td>$").append(product.getPrice()).append("</td>");
            html.append("<td class=\"actions-cell\">");
            html.append("<a href=\"/products/edit/").append(product.getId()).append("\" class=\"edit-link\">");
            html.append("<i class=\"fas fa-edit\"></i> Edit");
            html.append("</a>");
            html.append("<button class=\"delete-btn\" ");
            html.append("onclick=\"openDeleteDialog(").append(product.getId()).append(", '");
            html.append(escapeHtml(product.getTitle())).append("')\">");
            html.append("<i class=\"fas fa-trash\"></i> Delete");
            html.append("</button>");
            html.append("</td>");
            html.append("</tr>");
        }

        html.append("</tbody></table>");
        return html.toString();
    }

    private String buildSearchResultsHtml(List<Product> products, String query) {
        StringBuilder html = new StringBuilder();

        if (products.isEmpty()) {
            html.append("<div class=\"no-results\">");
            html.append("<i class=\"fas fa-search\"></i>");
            if (query.isEmpty()) {
                html.append("<p>Enter a search term to find products</p>");
            } else {
                html.append("<p>No products found for \"").append(escapeHtml(query)).append("\"</p>");
            }
            html.append("</div>");
            return html.toString();
        }

        html.append("<div class=\"results-count\">");
        html.append("<p>Found ").append(products.size()).append(" product(s)");
        if (!query.isEmpty()) {
            html.append(" for \"").append(escapeHtml(query)).append("\"");
        }
        html.append("</p></div>");

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