package com.mine.gallery.security;

import com.mine.gallery.persistence.entity.User;
import com.mine.gallery.persistence.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link UserDetailsService UserDetailsService} interface
 *
 * @author TrusTio
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Finds the user based on the username.
     * Uses the user's roles to create a list of authorities.
     *
     * @param username username used to find user
     * @return returns new
     * {@link org.springframework.security.core.userdetails.User org.springframework.security.core.userdetails.User}
     * with username, password and authorities
     * @throws UsernameNotFoundException if the username is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        List<GrantedAuthority> authorities = user.getRoles().stream().map(userRole ->
                new SimpleGrantedAuthority(userRole.getName().name())
        ).collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities);
    }
}