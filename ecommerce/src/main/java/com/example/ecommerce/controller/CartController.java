package com.example.ecommerce.controller;

import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    // Add product to cart
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestParam Long productId, @RequestParam int quantity) {
        // Validate product exists
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid product ID");
        }

        // Validate quantity is positive
        if (quantity <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quantity must be positive");
        }

        // Check if product already exists in the cart
        CartItem cartItem = cartItemRepository.findByProductId(productId);
        if (cartItem != null) {
            // Update quantity if product is already in cart
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
        } else {
            // Add new product to cart
            cartItem = new CartItem(product, quantity);
            cartItemRepository.save(cartItem);
        }

        return ResponseEntity.status(HttpStatus.OK).body("Product added to cart successfully.");
    }

    // Update cart
    @PutMapping("/update")
    public ResponseEntity<?> updateCart(@RequestParam Long productId, @RequestParam int newQuantity) {
        // Validate that the product exists in the cart
        CartItem cartItem = cartItemRepository.findByProductId(productId);
        if (cartItem == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product not found in cart");
        }

        // Ensure new quantity is non-negative
        if (newQuantity < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quantity must be non-negative");
        }

        // Check if the new quantity exceeds stock (assuming a method getStock exists in Product)
        if (newQuantity > cartItem.getProduct().getStock()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient stock");
        }

        // Update quantity or remove product
        if (newQuantity == 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
        }

        return ResponseEntity.status(HttpStatus.OK).body("Cart updated successfully.");
    }

    // Delete product from cart
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFromCart(@RequestParam Long productId) {
        // Validate that the product exists in the cart
        CartItem cartItem = cartItemRepository.findByProductId(productId);
        if (cartItem == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product not found in cart");
        }

        // Remove product from cart
        cartItemRepository.delete(cartItem);

        return ResponseEntity.status(HttpStatus.OK).body("Product removed from cart successfully.");
    }

    // Get all items in the cart
    @GetMapping("/")
    public ResponseEntity<?> getCart() {
        // Fetch all items from the cart
        List<CartItem> cartItems = cartItemRepository.findAll();

        // If the cart is empty, return a message indicating so
        if (cartItems.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("Your cart is empty.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(cartItems);
    }


}