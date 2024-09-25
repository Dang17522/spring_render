package com.zalo.Spring_Zalo.JWT;

public class SecurityContaints {
	
    public static final long JWT_ACCESS_TOKEN_EXPIRATON = 15 * 60 * 1000; // 15p
    public static final long JWT_REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000; // 7d
    public static final String JWT_SECRET = "secret";
}
