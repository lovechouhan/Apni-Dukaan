package com.eCommerce.Ecommerce.Exceptions;

public class OrderException extends RuntimeException {
    public OrderException(String message) {
        super(message);
    }
}