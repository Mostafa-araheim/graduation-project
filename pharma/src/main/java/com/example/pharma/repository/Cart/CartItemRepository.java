package com.example.pharma.repository.Cart;

import com.example.pharma.model.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> { }

