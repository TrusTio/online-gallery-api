package com.mine.gallery.persistence.repository;

import com.mine.gallery.persistence.entity.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for the User class/table
 *
 * @author TrusTio
 */
public interface UserRepository extends CrudRepository<User, Integer> {

}