package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.entities.Authority;
import com.valueinvesting.ruleone.entities.AuthorityType;
import com.valueinvesting.ruleone.repositories.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock private AppUserRepository appUserRepository;
    private CustomUserDetailsService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomUserDetailsService(appUserRepository);
    }

    @Test
    void checkIfLoadsUserByUsername() {
        AppUser appUser = new AppUser();
        appUser.setUsername("honggildong");
        appUser.setEmail("a@a.com");
        appUser.setEncryptedPassword("asdfasdfasdfasdf");
        Authority authority = new Authority();
        authority.setAppUser(appUser);
        appUser.setAuthority(authority);

        given(appUserRepository.findByUsername("honggildong"))
                .willReturn(Optional.of(appUser));

        assertThat(underTest.loadUserByUsername("honggildong").getAuthorities().toArray()[0].toString())
                .isEqualTo(AuthorityType.TRIAL.toString());
        assertThat(underTest.loadUserByUsername("honggildong").getUsername())
                .isEqualTo("honggildong");
    }

    @Test
    void checkIfLoadUserByUsernameThrowsUserNotFoundException() {
        given(appUserRepository.findByUsername(anyString()))
                .willReturn(Optional.empty());

        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> {
                    underTest.loadUserByUsername("honggildong");
                });
    }
}