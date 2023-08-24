package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.entities.Authority;
import com.valueinvesting.ruleone.entities.AuthorityType;
import com.valueinvesting.ruleone.exceptions.UserAlreadyExistException;
import com.valueinvesting.ruleone.exceptions.UserNotFoundException;
import com.valueinvesting.ruleone.repositories.AppUserRepository;
import com.valueinvesting.ruleone.security.JwtUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class AppUserServiceIntegrationTest {

    @PersistenceContext private EntityManager entityManager;
    @Autowired private AppUserRepository appUserRepository;
    @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private AppUserService underTest;

    private AppUser appUser;

    @BeforeEach
    void setUp() {
        appUser = new AppUser();
        appUser.setUsername("honggildong");
        appUser.setEmail("a@a.com");
        appUser.setEncryptedPassword("test123");
        Authority authority = new Authority();
        authority.setAppUser(appUser);
        appUser.setAuthority(new HashSet<>(List.of(authority)));
    }

    @Test
    void checkIfCreatesAppUser() {
        underTest.createAppUser(appUser);
        Optional<AppUser> optional = appUserRepository.findById(appUser.getId());

        assertThat(optional.isPresent()).isTrue();
        assertThat(optional.get().getUsername()).isEqualTo("honggildong");
        assertThat(optional.get().getEmail()).isEqualTo("a@a.com");
    }

    @Test
    void checkIfCreateAppUserEncodesPasswordCorrectly() {
        underTest.createAppUser(appUser);
        Optional<AppUser> optional = appUserRepository.findById(appUser.getId());

        assertThat(bCryptPasswordEncoder.matches(
                "test123",
                optional.get().getEncryptedPassword().replace("{bcrypt}", "")))
                .isTrue();
    }

    @Test
    void checkIfCreateAppUserThrowsExceptionWhenIdExists() {
        underTest.createAppUser(appUser);

        assertThatExceptionOfType(UserAlreadyExistException.class)
                .isThrownBy(() -> {
                    underTest.createAppUser(appUser);
                }).withMessageContaining("ID");
    }

    @Test
    void checkIfCreateAppUserThrowsExceptionWhenUsernameExists() {
        AppUser newUser = new AppUser();
        newUser.setUsername("honggildong");
        newUser.setEmail("a@a.com");
        newUser.setEncryptedPassword("test123");
        underTest.createAppUser(appUser);

        assertThatExceptionOfType(UserAlreadyExistException.class)
                .isThrownBy(() -> {
                    underTest.createAppUser(newUser);
                }).withMessageContaining("username");
    }

    @Test
    void checkIfGetsAppUserById() {
        appUserRepository.save(appUser);

        assertThat(underTest.getAppUserById(appUser.getId()).getUsername())
                .isEqualTo("honggildong");
        assertThat(underTest.getAppUserById(appUser.getId()).getEmail())
                .isEqualTo("a@a.com");
    }

    @Test
    void checkIfGetAppUserByIdThrowsExceptionWhenUserDoesNotExist() {
        appUserRepository.save(appUser);

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    underTest.getAppUserById(appUser.getId() + 1);
                }).withMessageContaining("ID");

    }

    @Test
    void checkIfGetsAppUserByUsername() {
        appUserRepository.save(appUser);

        assertThat(underTest.getAppUserByUsername("honggildong").getUsername())
                .isEqualTo("honggildong");
        assertThat(underTest.getAppUserByUsername("honggildong").getEmail())
                .isEqualTo("a@a.com");
    }

    @Test
    void checkIfGetAppUserByUsernameThrowsExceptionWhenUserDoesNotExist() {
        appUserRepository.save(appUser);

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    underTest.getAppUserByUsername("another");
                }).withMessageContaining("username");
    }

    @Test
    void checkIfUpdateUserUpdatesPassword() {
        underTest.createAppUser(appUser);

        underTest.updateUser(appUser.getId(), "another123", null);
        entityManager.flush();
        entityManager.refresh(appUser);
        Optional<AppUser> optional = appUserRepository.findById(appUser.getId());
        assertThat(bCryptPasswordEncoder.matches(
                "another123",
                optional.get().getEncryptedPassword().replace("{bcrypt}", "")
        )).isTrue();
    }

    @Test
    void checkIfUpdateUserUpdatesEmail() {
        underTest.createAppUser(appUser);

        underTest.updateUser(appUser.getId(), null, "b@b.com");
        entityManager.flush();
        entityManager.refresh(appUser);
        Optional<AppUser> optional = appUserRepository.findById(appUser.getId());
        assertThat("b@b.com").isEqualTo(optional.get().getEmail());
    }

    @Test
    void checkIfUpdateUserUpdatesPasswordAndEmail() {
        underTest.createAppUser(appUser);

        underTest.updateUser(appUser.getId(), "another123", "b@b.com");
        entityManager.flush();
        entityManager.refresh(appUser);
        Optional<AppUser> optional = appUserRepository.findById(appUser.getId());
        assertThat(bCryptPasswordEncoder.matches(
                "another123",
                optional.get().getEncryptedPassword().replace("{bcrypt}", "")
        )).isTrue();
        assertThat("b@b.com").isEqualTo(optional.get().getEmail());
    }

    @Test
    void checkIfUpdatesUserAuthority() {
        underTest.createAppUser(appUser);
        Authority authority1 = new Authority();
        authority1.setAuthority(AuthorityType.ESSENTIAL);
        authority1.setAppUser(appUser);
        Authority authority2 = new Authority();
        authority2.setAuthority(AuthorityType.ADMIN);
        authority2.setAppUser(appUser);
        Set<Authority> authorities = new HashSet<>(List.of(authority1, authority2));

        underTest.updateUserAuthority(appUser.getId(), authorities);

        Assertions.assertThat(appUserRepository.findByUsername(appUser.getUsername())
                        .get().getAuthority()).hasSize(2);
    }

    @Test
    void checkIfActivatesUser() {
        appUser.setActive(false);
        appUserRepository.save(appUser);

        underTest.activateUser(appUser.getId());
        entityManager.flush();
        entityManager.refresh(appUser);
        Optional<AppUser> optional = appUserRepository.findById(appUser.getId());

        assertThat(optional.get().isActive()).isTrue();
    }

    @Test
    void checkIfDeactivatesUser() {
        appUser.setActive(true);
        appUserRepository.save(appUser);

        underTest.deactivateUser(appUser.getId());
        entityManager.flush();
        entityManager.refresh(appUser);
        Optional<AppUser> optional = appUserRepository.findById(appUser.getId());

        assertThat(optional.get().isActive()).isFalse();
    }

    @Test
    void checkIfDeletesUser() {
        appUserRepository.save(appUser);

        underTest.deleteUser(appUser.getId());
        Optional<AppUser> optional = appUserRepository.findById(appUser.getId());

        assertThat(optional.isPresent()).isFalse();
    }

    @Test
    void checkIfDeleteUserThrowsExceptionWhenUserDoesNotExist() {
        appUserRepository.save(appUser);

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    underTest.deleteUser(appUser.getId() + 1);
                }).withMessageContaining("ID");
    }

    @Test
    void checkIfLogins() {
        underTest.createAppUser(appUser);
        entityManager.refresh(appUser);

        String jwt = underTest.login("honggildong", "test123");
        assertThat(jwtUtil.extractUsername(jwt)).isEqualTo("honggildong");
    }

    @Test
    void checkIfLoginFailsForBadCredentials() {
        underTest.createAppUser(appUser);
        entityManager.refresh(appUser);

        assertThatExceptionOfType(BadCredentialsException.class)
                .isThrownBy(() -> underTest.login("honggildong", "123123"));
    }
}