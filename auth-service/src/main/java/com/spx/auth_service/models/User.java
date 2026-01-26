package com.spx.auth_service.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private Long id;

    @NotBlank (message = "Username cannot be blank")
    @Size(min = 3, max = 50)
    @Indexed(unique = true)
    private String username;

    @NotBlank (message = "Password cannot be blank")
    @Size(min = 8)
    private String password;

    @Builder.Default
    private Set<Role> roles = Set.of(Role.USER);



}
