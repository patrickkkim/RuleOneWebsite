package com.valueinvesting.ruleone.entities;

import jakarta.validation.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AuthorityTest {
    @Autowired TestEntityManager testEntityManager;
    AppUser appUser;

    @BeforeEach
    void setUp() {
        appUser = new AppUser();
        appUser.setUsername("honggildong");
        appUser.setEmail("a@a.com");
        appUser.setEncryptedPassword("asdasdfasdf");
    }

    @Test
    void checkIfAuthorityCanBeInserted() {
        Authority authority = new Authority();
        appUser.setAuthority(new HashSet<>(List.of(authority)));
        authority.setAuthority(AuthorityType.ESSENTIAL);
        authority.setAppUser(appUser);

        int id = testEntityManager.persist(authority).getId();
        Authority newAuthority = testEntityManager.find(Authority.class, id);

        assertThat(newAuthority).isEqualTo(authority);
    }

    @Test
    void checkIfAuthorityDefaultValueIsWorking() {
        Authority authority = new Authority();
        appUser.setAuthority(new HashSet<>(List.of(authority)));
        authority.setAppUser(appUser);

        int id = testEntityManager.persist(authority).getId();
        Authority newAuthority = testEntityManager.find(Authority.class, id);

        assertThat(newAuthority.getAuthority()).isEqualTo(AuthorityType.TRIAL);
    }

    @Test
    void checkIfOtherAuthorityCannotBeInserted() {
        Authority authority = new Authority();
        authority.setAuthority(null);
        authority.setAppUser(appUser);

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> {
                    testEntityManager.persist(authority);
                }).withMessageContaining("must not be null");
    }
}