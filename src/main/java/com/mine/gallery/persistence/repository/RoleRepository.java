package com.mine.gallery.persistence.repository;

import com.mine.gallery.persistence.entity.Role;
import com.mine.gallery.persistence.entity.RoleName;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Repository for the {@link Role} class/table
 *
 * @author TrusTio
 */
public interface RoleRepository extends CrudRepository<Role, Integer> {
    Optional<Role> findByName(RoleName roleName);

}
