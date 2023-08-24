package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.entities.Authority;
import com.valueinvesting.ruleone.entities.AuthorityType;
import com.valueinvesting.ruleone.repositories.AppUserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;


@SpringBootTest
@Transactional
class CustomUserDetailsServiceIntegrationTest {

    @Autowired
    CustomUserDetailsService underTest;
    @Autowired AppUserRepository appUserRepository;

    @Test
    void checkIfLoadsUserByUsername() {
        AppUser appUser = new AppUser();
        appUser.setUsername("honggildong");
        appUser.setEmail("a@a.com");
        appUser.setEncryptedPassword("asdfasdfasdfasdf");
        Authority authority = new Authority();
        authority.setAuthority(AuthorityType.ESSENTIAL);
        authority.setAppUser(appUser);
        appUser.setAuthority(new HashSet<Authority>(List.of(authority)));

        appUserRepository.save(appUser);

        UserDetails userDetails = underTest.loadUserByUsername("honggildong");
        assertThat(userDetails.getAuthorities().toArray()[0].toString())
                .isEqualTo(AuthorityType.ESSENTIAL.toString());
    }

    @Test
    void checkIfLoadUserByUsernameThrowsUserNotFoundException() {
        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> {
                    underTest.loadUserByUsername("honggildong");
                });
    }
}