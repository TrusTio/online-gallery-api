package com.mine.gallery.service.dto;

import com.mine.gallery.persistence.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

/**
 * DTO class for the {@link com.mine.gallery.persistence.entity.User}
 *
 * @author TrusTio
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private Set<Role> roles = new HashSet<>();
}
