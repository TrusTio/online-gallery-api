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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

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
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            throw new AuthorizationServiceException("Missing Authorization header or 'Bearer' prefix.");
        }
        log.info("Checking authorization!");

        IdUsernamePasswordAuthenticationToken authenticationToken = getAuthentication(request);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }

    /**
     * Uses the header provided in the request parameter to get the token.
     * If the token isn't null, gets the user and claims(roles) from the token.
     *
     * @param request request parameter to be used for authentication
     * @return returns new {@link IdUsernamePasswordAuthenticationToken} with user and list of authorities(roles)
     * or null if the token has expired/is empty
     */
    private IdUsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

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
