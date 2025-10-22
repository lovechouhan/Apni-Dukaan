package com.eCommerce.Ecommerce.Exceptions;

public class CartItemException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CartItemException() {
        super();
    }

    public CartItemException(String message) {
        super(message);
    }

    public CartItemException(String message, Throwable cause) {
        super(message, cause);
    }

    public CartItemException(Throwable cause) {
        super(cause);
    }
}