package com.mine.gallery.exception.generic;

/**
 * Exception used when the access is unauthorized/denied.
 *
 * @author TrusTio
 */
public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
