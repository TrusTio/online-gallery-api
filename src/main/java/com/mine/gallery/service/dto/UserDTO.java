package com.mine.gallery.service.dto;


import com.mine.gallery.persistence.entity.Role;
import com.mine.gallery.validation.ValidEmail;
import com.mine.gallery.validation.ValidPassword;
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
    @ValidEmail
    private String email;
    @ValidPassword
    private String password;
    private Set<Role> roles;
}
