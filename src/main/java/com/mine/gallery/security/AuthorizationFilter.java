package com.mine.gallery.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static com.mine.gallery.security.SecurityConstants.SECRET;

/**
 * Authorization filter class that extends {@link BasicAuthenticationFilter}
 *
 * @author TrusTio
 */
@Slf4j
public class AuthorizationFilter extends BasicAuthenticationFilter {

    public AuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    /**
     * Checks for an authorization header, then checks if the Bearer prefix is present.
     * After that checks for token cookie.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        String token;
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer")) {
            Optional<Cookie> tokenCookie = Optional.empty();

            if (request.getCookies() != null) {
                tokenCookie = Arrays.stream(request.getCookies())
                        .filter(cookie -> cookie.getName().equals("token"))
                        .findFirst();

            }
            if (tokenCookie.isPresent()) {
                token = tokenCookie.get().getValue();
            } else {
                filterChain.doFilter(request, response);
                throw new AuthorizationServiceException("Missing token cookie or authorization header or 'bearer' prefix");
            }
        } else {
            token = request.getHeader("Authorization");
        }
        log.info("Checking authorization!");

        IdUsernamePasswordAuthenticationToken authenticationToken = getAuthentication(token);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }

    /**
     * Uses the token provided in the String parameter to
     * get the user and claims(roles) from the it.
     *
     * @param token String token to be used for authentication
     * @return returns new {@link IdUsernamePasswordAuthenticationToken} with user and list of authorities(roles)
     * or null if the token has expired/is empty
     */
    private IdUsernamePasswordAuthenticationToken getAuthentication(String token) {
        if (token != null) {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET.getBytes())
                    .parseClaimsJws(token.replace("Bearer", ""))
                    .getBody();

            if (new Date(System.currentTimeMillis()).after(claims.getExpiration())) {
                return null;
            }

            String user = claims.getSubject();
            Long id = claims.get("id", Long.class);

            ArrayList<String> roles = claims.get("roles", ArrayList.class);
            ArrayList<GrantedAuthority> list = new ArrayList<>();
            if (roles != null) {
                for (String a : roles) {
                    GrantedAuthority g = new SimpleGrantedAuthority(a);
                    list.add(g);
                }
            }

            if (user != null) {
                log.info("Authorization successful!");

                return new IdUsernamePasswordAuthenticationToken(id, user, id, list);
            }
            return null;
        }
        return null;
    }
}
