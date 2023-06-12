package com.valueinvesting.ruleone.repositories;

import com.valueinvesting.ruleone.entities.AppUser;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


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
    void checkIfUpdatedPasswordById() {
        AppUser appUser = new AppUser();
        appUser.setUsername("honggildong");
        appUser.setEmail("hong@gmail.com");
        appUser.setEncryptedPassword("asdfasdasdfasdf");
        int id = underTest.save(appUser).getId();

        String newPassword = "newpassword";
        underTest.updatePasswordById(id, newPassword);

        AppUser found = underTest.findById(id).get();
        testEntityManager.refresh(appUser);
        assertThat(found.getEncryptedPassword()).isEqualTo(newPassword);
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

        AppUser found = underTest.findById(id).get();
        testEntityManager.refresh(appUser);
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

        AppUser found = underTest.findById(id).get();
        testEntityManager.refresh(appUser);
        assertThat(found.isActive()).isEqualTo(active);
    }
}