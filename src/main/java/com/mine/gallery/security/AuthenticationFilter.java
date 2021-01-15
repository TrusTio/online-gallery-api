package com.mine.gallery.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mine.gallery.service.dto.UserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static com.mine.gallery.security.SecurityConstants.*;

/**
 * Authentication filter class that extends {@link UsernamePasswordAuthenticationFilter UsernamePasswordAuthenticationFilter}
 *
 * @author TrusTio
 */
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl("/api/v1/user/login");
    }


    /**
     * Attempts authentication using the user credentials(username and password)
     * <p>
     * {@inheritDoc}
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            UserDTO creds = new ObjectMapper()
                    .readValue(request.getInputStream(), UserDTO.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getUsername(),
                            creds.getPassword(),
                            new ArrayList<>()
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException("Could not read request" + e);
        }
    }

    /**
     * Takes the username and the roles of the user from the authentication parameter
     * and builds a token using them
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {

        if (authentication.getPrincipal() != null) {
            org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            String login = user.getUsername();

            if (login != null && login.length() > 0) {
                Claims claims = Jwts.claims().setSubject(login);
                List<String> roles = new ArrayList<>();

                user.getAuthorities().stream().forEach(
                        authority -> roles.add(authority.getAuthority())
                );
                claims.put("roles", roles);

                String token = Jwts.builder()
                        .setClaims(claims)
                        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                        .signWith(SignatureAlgorithm.HS512, SECRET.getBytes())
                        .compact();
                response.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
                Logger.getLogger(AuthenticationFilter.class.getName()).info("Successful authentication!");
            }

        }
    }
}
