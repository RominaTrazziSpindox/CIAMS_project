package com.spx.auth_service.repositories;

import com.spx.auth_service.models.Role;
import com.spx.auth_service.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {

    // User Login
    Optional<User> findByUsername(String username);

    // User Register
    boolean existsByUsername(String username);

    // User Delete
    void deleteByUsername(String username);

    // Find users by role and paginate
    Page<User> findByRoles(Role role, Pageable pageable);

}