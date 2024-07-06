package com.valueinvesting.ruleone.security;

import com.valueinvesting.ruleone.services.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil implements JwtSecretProvider {

    private byte[] secret;
    private final int expirationTime = 50 * 60 * 60 * 10;
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    public JwtUtil(CustomUserDetailsService customUserDetailsService) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        secret = Keys.secretKeyFor(signatureAlgorithm).getEncoded();
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public byte[] getSecretKey() {
        return secret;
    }

    public String generateToken(String username) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        return createToken(userDetails);
    }

    private String createToken(UserDetails userDetails) {
        List<String> authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .claim("authorities", createAuthorityMap(authorities))
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(Keys.hmacShaKeyFor(secret),
                        SignatureAlgorithm.HS256)
                .compact();
    }

    private Object createAuthorityMap(List<String> scopes) {
        return scopes.stream().collect(Collectors.toMap(
                scope -> "authority",
                scope -> new String[]{scope}
        ));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
