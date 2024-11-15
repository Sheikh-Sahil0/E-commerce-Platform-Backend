package com.example.ecommerce.dto;

import com.example.ecommerce.model.CartItem;

import java.util.List;

public class CartResponse {
    private List<CartItem> cartItems;
    private double totalAmount;

    public CartResponse(List<CartItem> cartItems, double totalAmount) {
        this.cartItems = cartItems;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}