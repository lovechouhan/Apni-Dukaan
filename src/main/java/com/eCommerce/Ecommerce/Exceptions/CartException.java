package com.eCommerce.Ecommerce.Exceptions;

public class CartException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CartException() {
        super();
    }

    public CartException(String message) {
        super(message);
    }

    public CartException(String message, Throwable cause) {
        super(message, cause);
    }

    public CartException(Throwable cause) {
        super(cause);
    }
}