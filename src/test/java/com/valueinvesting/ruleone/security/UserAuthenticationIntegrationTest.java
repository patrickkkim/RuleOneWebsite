package com.valueinvesting.ruleone.security;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.entities.Authority;
import com.valueinvesting.ruleone.entities.AuthorityType;
import com.valueinvesting.ruleone.services.AppUserService;
import com.valueinvesting.ruleone.services.CustomUserDetailsService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;


@SpringBootTest
@Transactional
class UserAuthenticationIntegrationTest {

    @Autowired private AppUserService appUserService;
    @Autowired private CustomUserDetailsService customUserDetailsService;
    @Autowired private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        AppUser appUser = new AppUser();
        appUser.setUsername("honggildong");
        appUser.setEncryptedPassword("password123");
        appUser.setEmail("a@a.com");
        Authority authority = new Authority();
        authority.setAppUser(appUser);
        appUser.setAuthority(new HashSet<>(List.of(authority)));
        appUserService.createAppUser(appUser);
    }

    @Test
    void checkIfUserAuthenticatesWithValidUser() {
        String username = "honggildong";
        String password = "password123";

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );

        assertThat(authentication.getAuthorities().toArray()[0].toString())
                .isEqualTo(AuthorityType.TRIAL.toString());
        assertThat(authentication.isAuthenticated()).isTrue();
    }

    @Test
    void checkIfUserAuthenticatesWithWrongPassoword() {
        String username = "honggildong";
        String password = "123123123";

        assertThatExceptionOfType(BadCredentialsException.class)
                .isThrownBy(() -> {
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(username, password)
                    );
                });
    }
}