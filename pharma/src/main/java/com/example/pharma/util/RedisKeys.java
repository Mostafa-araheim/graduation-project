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

    public static String signupToken(String token) {
        return "auth:signup:token:" + token;
    }

    public static String signupEmail(String email) {
        return "auth:signup:email:" + email;
    }

    public static String signupSession(String signupId) {
        return "auth:signup:" + signupId;
    }

    public static String customerSignupSession(String signupId) {
        return "customer:signup:" + signupId;
    }

    public static String ownerSignupSession(String signupId) {
        return "owner:signup:" + signupId;
    }
}