package com.mine.gallery.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
import java.util.logging.Logger;

import static com.mine.gallery.security.SecurityConstants.SECRET;

/**
 * Authorization filter class that extends {@link BasicAuthenticationFilter BasicAuthenticationFilter}
 *
 * @author TrusTio
 */
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
            return;
        }
        Logger.getLogger(AuthorizationFilter.class.getName()).info("Getting authentication!");
        UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }

    /**
     * Uses the header provided in the request parameter to get the token.
     * If the token isn't null, gets the user and claims(roles) from the token.
     *
     * @param request request parameter to be used for authentication
     * @return returns new UsernamePasswordAuthenticationToken with user and list of authorities(roles)
     * or null if the token has expired/is empty
     */
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
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

            ArrayList<String> roles = (ArrayList<String>) claims.get("roles");
            ArrayList<GrantedAuthority> list = new ArrayList<>();
            if (roles != null) {
                for (String a : roles) {
                    GrantedAuthority g = new SimpleGrantedAuthority(a);
                    list.add(g);
                }
            }

            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, list);
            }
            return null;
        }
        return null;
    }
}
