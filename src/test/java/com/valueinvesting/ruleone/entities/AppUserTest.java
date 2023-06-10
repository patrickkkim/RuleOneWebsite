package com.valueinvesting.ruleone.entities;

import jakarta.validation.ConstraintViolationException;
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

    @Test
    void checkIfUserCanBeInserted() {
        String name = "honggildong";
        String password = "asdfasdfasdfasdf";
        String email = "asdf@naver.com";
        AppUser appUser = new AppUser(name, password, email);

        int id = testEntityManager.persist(appUser).getId();
        AppUser newUser = testEntityManager.find(AppUser.class, id);

        assertThat(newUser).isEqualTo(appUser);
    }

    @Test
    void checkIfUserCantBeInsertedWhenEmailIsWrong() {
        String name = "honggildong";
        String password = "asdfasdfasdfasdf";
        String email = "aaaaa@.";
        AppUser appUser = new AppUser(name, password, email);

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> {
                    testEntityManager.persist(appUser);
                })
                .withMessageContaining("must be a well-formed email address");
    }

    @Test
    void checkIfUserCantBeInsertedWhenUsernameIsShort() {
        String name = "asdf";
        String password = "asdfasdfasdfasdf";
        String email = "asdf@naver.com";
        AppUser appUser = new AppUser(name, password, email);

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> {
                    testEntityManager.persist(appUser);
                })
                .withMessageContaining("size must be between");
    }

    @Test
    void checkIfUserCantBeInsertedWhenPasswordIsBlank() {
        String name = "honggildong";
        String password = "";
        String email = "asdf@naver.com";
        AppUser appUser = new AppUser(name, password, email);

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> {
                    testEntityManager.persist(appUser);
                })
                .withMessageContaining("must not be blank");
    }

    @Test
    void checkIfUserCantBeInsertedWhenPasswordIsNull() {
        String name = "honggildong";
        String password = null;
        String email = "asdf@naver.com";
        AppUser appUser = new AppUser(name, password, email);

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> {
                    testEntityManager.persist(appUser);
                })
                .withMessageContaining("must not be blank");
    }
}