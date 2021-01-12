package com.mine.gallery.service.dto;


import com.mine.gallery.persistence.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * DTO class for the {@link com.mine.gallery.persistence.entity.User User}
 *
 * @author TrusTio
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserDTO {
    private String username;
    private String email;
    private String password;
    private Set<UserRole> roles;
}
