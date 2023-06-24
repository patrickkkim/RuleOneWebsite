package com.valueinvesting.ruleone.exceptions;

public class SubscriptionAlreadyExistException extends RuntimeException {
    public SubscriptionAlreadyExistException(String message) {
        super(message);
    }
}
