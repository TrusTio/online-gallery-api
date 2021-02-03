package com.mine.gallery.exception.user;

/**
 * Exception used for when a User was not found.
 *
 * @author TrusTio
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super(String.format("User with id '%d' not found!", id));
    }
}