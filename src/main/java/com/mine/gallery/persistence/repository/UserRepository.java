package com.mine.gallery.persistence.repository;

import com.mine.gallery.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for the {@link User} class/table
 *
 * @author TrusTio
 */
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}