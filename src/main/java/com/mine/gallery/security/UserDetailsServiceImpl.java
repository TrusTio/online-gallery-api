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
 * Implementation of the {@link UserDetailsService} interface
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
     * @return returns new {@link CurrentUser} with username, password and authorities
     * @throws {@link UsernameNotFoundException} if the username wan not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        List<GrantedAuthority> authorities = user.getRoles().stream().map(userRole ->
                new SimpleGrantedAuthority(userRole.getName().name())
        ).collect(Collectors.toList());

        return new CurrentUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                authorities);
    }
}