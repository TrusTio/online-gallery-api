package com.mine.gallery.exception.role;

/**
 * Exception used for when a Role was not found.
 *
 * @author TrusTio
 */
public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException() {
        super("No Role like that exists.");

    }
}