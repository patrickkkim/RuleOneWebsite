package com.valueinvesting.ruleone.entities;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@DataJpaTest
class UserTest {

    @Autowired private TestEntityManager testEntityManager;
    @Mock private Subscription subscription;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void itShouldCheckIfUserCanBeInserted() {
        String name = "honggildong";
        String password = "asdfasdfasdfasdf";
        String email = "asdf@naver.com";
        AppUser appUser = new AppUser(name, password, email, subscription);

        int id = testEntityManager.persist(appUser).getId();
        AppUser newUser = testEntityManager.find(AppUser.class, id);

        assertThat(newUser).isSameAs(appUser);
    }

    @Test
    void itShouldCheckIfUserCantBeInsertedWhenEmailIsWrong() {
        String name = "honggildong";
        String password = "asdfasdfasdfasdf";
        String email = "aaaaa@.";
        AppUser appUser = new AppUser(name, password, email, subscription);

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> {
                    testEntityManager.persist(appUser);
                })
                .withMessageContaining("must be a well-formed email address");
    }

    @Test
    void itShouldCheckIfUserCantBeInsertedWhenUsernameIsShort() {
        String name = "asdf";
        String password = "asdfasdfasdfasdf";
        String email = "asdf@naver.com";
        AppUser appUser = new AppUser(name, password, email, subscription);

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> {
                    testEntityManager.persist(appUser);
                })
                .withMessageContaining("size must be between");
    }

    @Test
    void itShouldCheckIfUserCantBeInsertedWhenPasswordIsBlank() {
        String name = "honggildong";
        String password = "";
        String email = "asdf@naver.com";
        AppUser appUser = new AppUser(name, password, email, subscription);

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> {
                    testEntityManager.persist(appUser);
                })
                .withMessageContaining("must not be blank");
    }

    @Test
    void itShouldCheckIfUserCantBeInsertedWhenPasswordIsNull() {
        String name = "honggildong";
        String password = null;
        String email = "asdf@naver.com";
        AppUser appUser = new AppUser(name, password, email, subscription);

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> {
                    testEntityManager.persist(appUser);
                })
                .withMessageContaining("must not be blank");
    }
}