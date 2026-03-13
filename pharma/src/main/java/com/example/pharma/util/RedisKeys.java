package com.example.pharma.util;

public final class RedisKeys {

    private RedisKeys() {
    }

    // type in redis -> SET
    public static String userCarts(Long userId) {
        return "user:" + userId + ":carts";
    }

    // type in redis -> HASH
    public static String cart(Long cartId) {
        return "cart:" + cartId;
    }

    // type in redis -> HASH
    public static String cartItems(Long cartId) {
        return "cart:" + cartId + ":items";
    }

    // type in redis -> STRING
    public static String signupSession(String signupId) {
        return "auth:signup:" + signupId;
    }

    // type in redis -> STRING
    public static String loginSession(String loginId) {
        return "auth:login:" + loginId;
    }

    public static String cartItemField(Long inventoryRecordId) {
        return inventoryRecordId.toString();
    }


}