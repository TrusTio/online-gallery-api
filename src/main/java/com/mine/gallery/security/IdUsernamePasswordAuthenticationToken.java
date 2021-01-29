package com.mine.gallery.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Custom implementation of the {@link UsernamePasswordAuthenticationToken UsernamePasswordAuthenticationToken}
 * Has the user id as well.
 *
 * @author TrusTio
 */
public class IdUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private Long id;

    public IdUsernamePasswordAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public IdUsernamePasswordAuthenticationToken(Long id, Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
