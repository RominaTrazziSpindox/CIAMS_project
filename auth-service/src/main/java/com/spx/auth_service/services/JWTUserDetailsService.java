package com.spx.auth_service.services;

import com.spx.auth_service.models.User;
import com.spx.auth_service.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JWTUserDetailsService implements UserDetailsService {

    // Constructor injection
    private final UserRepository userRepository;

    public JWTUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Override Spring Security method to extract the user data (User from DB is not equal to UserDetail of Spring Security)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Step 1: Retrieve a User from our Mongodb
        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            log.error("User not found with username: {}", username);
            return new UsernameNotFoundException("User not found with username: " + username );
        });

        // Step 2: Retrieve its role converting Set<Role> in a Granted Authority collection
        Collection<GrantedAuthority> authorities = user
                .getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());

        // Step 3: Return a UserDetails object with details
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}



