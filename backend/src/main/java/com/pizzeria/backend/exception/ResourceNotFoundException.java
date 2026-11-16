package com.pizzeria.backend.exception;

public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String resourceName, Object id) {
        super(resourceName + " with id " + id + " not found");
    }
}