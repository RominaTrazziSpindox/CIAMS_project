package com.spx.auth_service.exceptions;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String identifier;

    public ResourceNotFoundException(String resourceName, String identifier) {
        this.resourceName = resourceName;
        this.identifier = identifier;
    }
}
