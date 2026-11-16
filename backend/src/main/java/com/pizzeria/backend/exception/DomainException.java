package com.pizzeria.backend.exception;

public abstract class DomainException extends RuntimeException{

	protected DomainException(String message) {
		super(message);
	}
}
