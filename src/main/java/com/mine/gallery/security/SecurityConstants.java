package com.mine.gallery.security;

/**
 * Security constants class that contains some constants used through the security classes
 *
 * @author TrusTio
 */
class SecurityConstants {
    public static final String SECRET = "SECRET_KEY";
    public static final long EXPIRATION_TIME = 900_000; // 15 mins
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/api/v1/user/signup";
    private SecurityConstants() {
    }
}
