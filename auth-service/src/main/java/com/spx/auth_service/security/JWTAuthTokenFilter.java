package com.spx.auth_service.security;

import com.spx.auth_service.services.JWTUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;


/**
 * This filter:
 * - validates incoming JWTs
 * - loads full user details from the database
 * - builds a complete Spring Security Authentication
 */
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

            // STEP 1: Extract JWT from Authorization header (Bearer <token>)
            String jwt = extractJwt(request);

            // If there is a JWT, it is valid and SecurityContext is null...
            if (jwt != null && jwtUtils.validateToken(jwt) && SecurityContextHolder.getContext().getAuthentication() == null)  {

                // STEP 2: Extract full user details associated with the token
                final String username = jwtUtils.getUserFromToken(jwt);
                final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);

                // STEP 3: Build Authentication object using UserDetails
                UsernamePasswordAuthenticationToken authenticationToken =  new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // STEP 4: Store the Authentication object in the SecurityContext.
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e);
        }

        // STEP 5: Keep on with the next filter
        filterChain.doFilter(request, response);

    }

    // Extracts an incoming JWT attached to an HTTP request (it is stored in Authorization header)
    private String extractJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith(BEARER)) {
            // Delete "Bearer " label
            return headerAuth.substring(BEARER.length());
        }
        return null;
    }
}


