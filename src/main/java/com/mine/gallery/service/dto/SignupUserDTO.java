package com.mine.gallery.service.dto;

import com.mine.gallery.validation.ValidEmail;
import com.mine.gallery.validation.ValidPassword;
import com.mine.gallery.validation.ValidUsername;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * DTO class for the {@link com.mine.gallery.persistence.entity.User User}
 *
 * @author TrusTio
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SignupUserDTO {
    @ValidUsername
    private String username;
    @ValidEmail
    private String email;
    @ValidPassword
    private String password;
}
