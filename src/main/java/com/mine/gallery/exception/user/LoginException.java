package com.mine.gallery.exception.user;

/**
 * Exception used for when the login fails.
 *
 * @author TrusTio
 */
public class LoginException extends RuntimeException {
    public LoginException(String message) {
        super(message);
    }
}