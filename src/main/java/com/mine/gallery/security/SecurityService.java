package com.mine.gallery.security;

import com.mine.gallery.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A service class that holds methods used in
 * {@link org.springframework.security.access.prepost.PreAuthorize PreAuthorize}
 * and
 * {@link org.springframework.security.access.prepost.PostAuthorize PostAuthorize}
 * annotations.
 *
 * @author TrusTio
 */
@Service
public class SecurityService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Checks if the username equals to the username of the user with the given id.
     *
     * @param id       Long id of the user
     * @param username String username to be compared to
     * @return true if the username equals to the username of the user
     */
    public boolean hasAccess(Long id, String username) {
        return userRepository.findById(id).get().getUsername().equals(username);
    }
}
