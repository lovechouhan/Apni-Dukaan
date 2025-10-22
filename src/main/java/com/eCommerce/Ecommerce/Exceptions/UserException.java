package com.eCommerce.Ecommerce.Exceptions;

public class UserException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserException() {
        super();
    }

    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserException(Throwable cause) {
        super(cause);
    }

    // Common user-related error messages
    public static final String USER_NOT_FOUND = "User not found";
    public static final String INVALID_CREDENTIALS = "Invalid username or password";
    public static final String EMAIL_ALREADY_EXISTS = "Email already registered";
    public static final String USERNAME_ALREADY_EXISTS = "Username already taken";
    public static final String INVALID_TOKEN = "Invalid or expired token";
    public static final String UNAUTHORIZED_ACCESS = "Unauthorized access";
}