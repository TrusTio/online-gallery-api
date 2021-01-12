package com.mine.gallery;

import com.mine.gallery.persistence.entity.RoleName;
import com.mine.gallery.persistence.entity.UserRole;
import com.mine.gallery.persistence.repository.UserRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Starting point of the application. The only functionality
 * it has currently is connecting to a MySQL and creating a database.
 * You can also check the swagger ui on http://localhost:8080/swagger-ui.html
 * It can be found on http://localhost:8080/
 *
 * @author TrusTio
 */
@SpringBootApplication
public class GalleryApplication {

    public static void main(String[] args) {
        SpringApplication.run(GalleryApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner demoData(UserRoleRepository userRoleRepository) {
        return  args -> {
            userRoleRepository.save(new UserRole(1L, RoleName.ROLE_ADMIN));
            userRoleRepository.save(new UserRole(2L, RoleName.ROLE_USER));
        };
    }
}
