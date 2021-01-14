package com.mine.gallery.exception.user;

/**
 * Exception used for when the sign up validation of fails.
 *
 * @author TrusTio
 */
public class SignUpValidationException extends RuntimeException {
    public SignUpValidationException(String message) {
        super(message);
    }
}