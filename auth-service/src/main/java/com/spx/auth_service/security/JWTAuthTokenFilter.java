package com.spx.auth_service.security;

import com.spx.auth_service.services.JWTUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class JWTAuthTokenFilter extends OncePerRequestFilter {

    public static final String BEARER = "Bearer ";

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private JWTUserDetailsService jwtUserDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {

            // STEP 1: Try to parse the HTTP Authorization header (expects format: "Bearer <token>")
            String jwt = parseJwt(request);

            // If there is a JWT, and it is valid...
            if (jwt != null && jwtUtils.validateToken(jwt)) {

                // STEP 2: Extract username from the JWT and load full user details from the database (roles, password hash, account status)
                final String username = jwtUtils.getUserFromToken(jwt);
                final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);

                // STEP 3: Create an Authentication object. This marks the user as authenticated for the current request
                UsernamePasswordAuthenticationToken authenticationToken =  new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // STEP 4: Attach additional request-related details (IP, session ID). Useful for auditing, logging and debugging purposes
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                /* STEP 5: Store the Authentication in the SecurityContext. From this point on, Spring Security considers the user authenticated
                and authorization annotations (@PreAuthorize, hasRole, etc.) will work */
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e);
        }

        // STEP 6: Keep on with the next filter
        filterChain.doFilter(request, response);

    }

    // Try to parse an incoming JWT attached to a GET request from client
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith(BEARER)) {
            // Delete "Bearer " label
            return headerAuth.substring(BEARER.length());
        }
        return null;
    }
}


