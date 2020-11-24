package com.mine.gallery.persistence.repository;

import com.mine.gallery.persistence.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

}