package com.valueinvesting.ruleone.entities;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@DataJpaTest
class AppUserTest {

    @Autowired private TestEntityManager testEntityManager;
    AppUser appUser;

    @BeforeEach
    void setUp() {
        String name = "honggildong";
        String password = "asdfasdfasdfasdf";
        String email = "asdf@naver.com";
        Authority authority = new Authority();
        appUser = new AppUser();
        appUser.setAuthority(authority);
        authority.setAppUser(appUser);
        authority.setAuthority(AuthorityType.ESSENTIAL);
        appUser.setUsername(name);
        appUser.setEmail(email);
        appUser.setEncryptedPassword(password);
    }

    @Test
    void checkIfUserCanBeInserted() {
        int id = testEntityManager.persist(appUser).getId();
        AppUser newUser = testEntityManager.find(AppUser.class, id);

        assertThat(newUser).isEqualTo(appUser);
    }

    @Test
    void checkIfUserCantBeInsertedWhenEmailIsWrong() {
        String email = "aaaaa@.";
        appUser.setEmail(email);

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> {
                    testEntityManager.persist(appUser);
                })
                .withMessageContaining("must be a well-formed email address");
    }

    @Test
    void checkIfUserCantBeInsertedWhenUsernameIsShort() {
        String name = "asdf";
        appUser.setUsername(name);

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> {
                    testEntityManager.persist(appUser);
                })
                .withMessageContaining("size must be between");
    }

    @Test
    void checkIfUserCantBeInsertedWhenPasswordIsBlank() {
        appUser.setEncryptedPassword("");

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> {
                    testEntityManager.persist(appUser);
                })
                .withMessageContaining("must not be blank");
    }

    @Test
    void checkIfUserCantBeInsertedWhenPasswordIsNull() {
        appUser.setEncryptedPassword(null);

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> {
                    testEntityManager.persist(appUser);
                })
                .withMessageContaining("must not be blank");
    }
}