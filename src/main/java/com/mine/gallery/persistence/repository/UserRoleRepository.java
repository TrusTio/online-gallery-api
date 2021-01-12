package com.mine.gallery.persistence.repository;

import com.mine.gallery.persistence.entity.RoleName;
import com.mine.gallery.persistence.entity.UserRole;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Repository for the Role class/table
 *
 * @author TrusTio
 */
public interface UserRoleRepository extends CrudRepository<UserRole, Integer> {
        Optional<UserRole> findByName(RoleName roleName);
}
