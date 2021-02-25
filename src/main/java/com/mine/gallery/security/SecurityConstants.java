package com.mine.gallery.security;

/**
 * Security constants class that contains some constants used through the security classes
 *
 * @author TrusTio
 */
class SecurityConstants {
    public static final String SECRET = "SECRET_KEY";
    public static final long EXPIRATION_TIME = 86_400_000; // 24h
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/api/v1/users/signup";

    private SecurityConstants() {
    }
}
