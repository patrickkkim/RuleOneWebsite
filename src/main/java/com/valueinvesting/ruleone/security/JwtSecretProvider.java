package com.valueinvesting.ruleone.security;

public interface JwtSecretProvider {
    byte[] getSecretKey();
}
