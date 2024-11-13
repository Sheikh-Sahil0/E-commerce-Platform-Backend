package com.example.ecommerce.controller;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // Add Product with Validation
    @PostMapping("/addproduct")
    public ResponseEntity<?> addProduct(@Valid @RequestBody Product product) {
        if (product.getPrice() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Price should be a positive number!");
        }

        productRepository.save(product);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Product added successfully! Product ID: " + product.getId());
    }

    // Get All Products
    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No products found.");
        }
        return ResponseEntity.ok(products);
    }

    // Get Product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Product with ID " + id + " not found!"));
    }

    // Update Product with Validation
    @PutMapping("/updateproduct/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody Product updatedProduct) {
        return productRepository.findById(id)
                .map(product -> {
                    // Update only valid and non-empty fields
                    if (updatedProduct.getName() != null && !updatedProduct.getName().isEmpty()) {
                        product.setName(updatedProduct.getName());
                    }
                    if (updatedProduct.getDescription() != null && !updatedProduct.getDescription().isEmpty()) {
                        product.setDescription(updatedProduct.getDescription());
                    }
                    if (updatedProduct.getPrice() != null && updatedProduct.getPrice() > 0) {
                        product.setPrice(updatedProduct.getPrice());
                    } else if (updatedProduct.getPrice() != null && updatedProduct.getPrice() <= 0) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Price must be a positive number!");
                    }
                    if (updatedProduct.getCategory() != null && !updatedProduct.getCategory().isEmpty()) {
                        product.setCategory(updatedProduct.getCategory());
                    }

                    // Save updated product
                    productRepository.save(product);
                    return ResponseEntity.ok("Product updated successfully!");
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Product with ID " + id + " not found!"));
    }


    // Delete Product with Validation
    @DeleteMapping("/deleteproduct/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return ResponseEntity.ok("Product deleted successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product with ID " + id + " not found!");
        }
    }
}
