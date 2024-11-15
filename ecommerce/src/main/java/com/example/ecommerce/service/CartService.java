package com.example.ecommerce.service;

import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    // Add product to cart
    public String addToCart(Long productId, int quantity) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new IllegalArgumentException("Invalid product ID");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        CartItem cartItem = cartItemRepository.findByProductId(productId);
        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
        } else {
            cartItem = new CartItem(product, quantity);
            cartItemRepository.save(cartItem);
        }

        return "Product added to cart successfully.";
    }

    // Update cart
    public String updateCart(Long productId, int newQuantity) {
        CartItem cartItem = cartItemRepository.findByProductId(productId);
        if (cartItem == null) {
            throw new IllegalArgumentException("Product not found in cart");
        }

        if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantity must be non-negative");
        }

        if (newQuantity == 0) {
            cartItemRepository.delete(cartItem);
        } else {
            if (newQuantity > cartItem.getProduct().getStock()) {
                throw new IllegalArgumentException("Insufficient stock");
            }
            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
        }

        return "Cart updated successfully.";
    }

    // Delete product from cart
    public String deleteFromCart(Long productId) {
        CartItem cartItem = cartItemRepository.findByProductId(productId);
        if (cartItem == null) {
            throw new IllegalArgumentException("Product not found in cart");
        }

        cartItemRepository.delete(cartItem);

        return "Product removed from cart successfully.";
    }
}