package com.mine.gallery.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Custom implementation of {@link org.springframework.security.core.userdetails.User}.
 * Holds the user id as well.
 *
 * @author TrusTio
 */
public class CurrentUser extends User {
    private final Long id;

    public CurrentUser(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
