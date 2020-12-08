package com.mine.gallery.persistence.repository;

import com.mine.gallery.persistence.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Repository for the User class/table
 *
 * @author TrusTio
 */
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findById(Long id);

    boolean existsByUsername (String username);

    boolean existsByEmail(String email);
}