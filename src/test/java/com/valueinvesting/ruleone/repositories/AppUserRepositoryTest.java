package com.valueinvesting.ruleone.repositories;

import com.valueinvesting.ruleone.entities.AppUser;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;


@DataJpaTest
class AppUserRepositoryTest {

    @Autowired private TestEntityManager testEntityManager;
    @Autowired private AppUserRepository underTest;

    @Test
    void checkIfFoundByUsername() {
        AppUser appUser = new AppUser();
        String username = "honggildong";
        appUser.setUsername(username);
        appUser.setEmail("hong@gmail.com");
        appUser.setEncryptedPassword("asdfasdasdfasdf");
        underTest.save(appUser);

        Optional<AppUser> foundAppUserOptional = underTest.findByUsername(username);

        assertThat(foundAppUserOptional.isPresent()).isTrue();
        AppUser found = foundAppUserOptional.get();
        assertThat(found).isEqualTo(appUser);
    }

    @Test
    void checkIfNotFoundByUsername() {
        AppUser appUser = new AppUser();
        String username = "honggildong";
        appUser.setUsername(username);
        appUser.setEmail("hong@gmail.com");
        appUser.setEncryptedPassword("asdfasdasdfasdf");
        underTest.save(appUser);

        Optional<AppUser> foundAppUserOptional = underTest.findByUsername("asdf");

        assertThat(foundAppUserOptional.isPresent()).isFalse();
    }

    @Test
    void checkIfUpdatedPasswordById() {
        AppUser appUser = new AppUser();
        appUser.setUsername("honggildong");
        appUser.setEmail("hong@gmail.com");
        appUser.setEncryptedPassword("asdfasdasdfasdf");
        int id = underTest.save(appUser).getId();

        String newPassword = "newpassword";
        underTest.updatePasswordById(id, newPassword);
        testEntityManager.refresh(appUser);

        AppUser found = underTest.findById(id).get();
        assertThat(found.getEncryptedPassword()).isEqualTo(newPassword);
    }

    @Test
    void checkIfNotUpdatedPasswordByIdWhenPasswordIsNull() {
        AppUser appUser = new AppUser();
        appUser.setUsername("honggildong");
        appUser.setEmail("hong@gmail.com");
        appUser.setEncryptedPassword("asdfasdasdfasdf");
        int id = underTest.save(appUser).getId();

        String newPassword = null;

        assertThatExceptionOfType(DataIntegrityViolationException.class)
                .isThrownBy(() -> {
                    underTest.updatePasswordById(id, newPassword);
                })
                .withMessageContaining("NULL not allowed");
    }

    @Test
    void checkIfUpdatedEmailById() {
        AppUser appUser = new AppUser();
        appUser.setUsername("honggildong");
        appUser.setEmail("hong@gmail.com");
        appUser.setEncryptedPassword("asdfasdasdfasdf");
        int id = underTest.save(appUser).getId();

        String newEmail = "asdf@naver.com";
        underTest.updateEmailById(id, newEmail);
        testEntityManager.refresh(appUser);

        AppUser found = underTest.findById(id).get();
        assertThat(found.getEmail()).isEqualTo(newEmail);
    }

    @Test
    void checkIfUpdatedActiveById() {
        AppUser appUser = new AppUser();
        appUser.setUsername("honggildong");
        appUser.setEmail("hong@gmail.com");
        appUser.setEncryptedPassword("asdfasdasdfasdf");
        int id = underTest.save(appUser).getId();

        boolean active = false;
        underTest.updateActiveById(id, active);
        testEntityManager.refresh(appUser);

        AppUser found = underTest.findById(id).get();
        assertThat(found.isActive()).isEqualTo(active);
    }
}