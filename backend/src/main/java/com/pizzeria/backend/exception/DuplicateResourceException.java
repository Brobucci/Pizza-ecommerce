package com.pizzeria.backend.exception;

public class DuplicateResourceException extends DomainException{

	public DuplicateResourceException(String resourceName, String field, Object value) {
        super(resourceName + " with " + field + " '" + value + "' already exists");
    }


}
