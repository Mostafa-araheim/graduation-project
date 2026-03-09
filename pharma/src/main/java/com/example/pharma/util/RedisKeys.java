package com.example.pharma.util;

public final class RedisKeys {

    private RedisKeys() {
    }

    public static String userCarts(Long userId) {
        return "user:" + userId + ":carts";
    }

    public static String cart(Long cartId) {
        return "cart:" + cartId;
    }

    public static String cartItems(Long cartId) {
        return "cart:" + cartId + ":items";
    }

    public static String signupSession(String signupId) {
        return "auth:signup:" + signupId;
    }

    public static String loginSession(String loginId) {
        return "auth:login:" + loginId;
    }

    public static String cartItemField(Long inventoryId, Long medicineId) {
        return "inventoryId:" + inventoryId + ":medicineId:" + medicineId;
    }

}