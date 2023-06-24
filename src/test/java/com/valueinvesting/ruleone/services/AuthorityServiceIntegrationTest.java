package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.entities.Authority;
import com.valueinvesting.ruleone.entities.AuthorityType;
import com.valueinvesting.ruleone.exceptions.AuthorityAlreadyExistException;
import com.valueinvesting.ruleone.exceptions.AuthorityNotFoundException;
import com.valueinvesting.ruleone.repositories.AuthorityRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class AuthorityServiceIntegrationTest {

    @PersistenceContext private EntityManager entityManager;
    @Autowired private AuthorityRepository authorityRepository;
    @Autowired private AuthorityService underTest;
    private Authority authority;

    @BeforeEach
    void setUp() {
        AppUser appUser = new AppUser();
        appUser.setUsername("honggildong");
        appUser.setEmail("a@a.com");
        appUser.setEncryptedPassword("123123123");
        authority = new Authority();
        authority.setAppUser(appUser);
    }

    @Test
    void checkIfCreatesAuthority() {
        underTest.createAuthority(authority);

        Optional<Authority> optional = authorityRepository.findById(authority.getId());
        assertThat(optional.isPresent()).isTrue();
        assertThat(optional.get()).isEqualTo(authority);
    }

    @Test
    void checkIfCreateAuthorityThrowsExceptionWhenAuthorityExists() {
        underTest.createAuthority(authority);

        assertThatExceptionOfType(AuthorityAlreadyExistException.class)
                .isThrownBy(() -> {
                    underTest.createAuthority(authority);
                }).withMessageContaining("ID");
    }

    @Test
    void checkIfGetsAuthorityById() {
        authorityRepository.save(authority);
        Authority newAuthority = underTest.getAuthorityById(authority.getId());

        assertThat(newAuthority).isEqualTo(authority);
    }

    @Test
    void checkIfGetAuthorityByIdThrowsExceptionWhenAuthorityDoesNotExist() {
        assertThatExceptionOfType(AuthorityNotFoundException.class)
                .isThrownBy(() -> {
                    underTest.getAuthorityById(authority.getId());
                }).withMessageContaining("ID");
    }

    @Test
    void checkIfGetsAuthorityByAppUser() {
        authorityRepository.save(authority);
        Authority newAuthority = underTest.getAuthorityByAppUser(authority.getAppUser());

        assertThat(newAuthority).isEqualTo(authority);
    }

    @Test
    void checkIfGetAuthorityByAppUserThrowsExceptionWhenAuthorityDoesNotExist() {
        authorityRepository.save(authority);
        AppUser appUser = new AppUser();
        appUser.setUsername("Another");
        appUser.setEmail("b@b.com");
        appUser.setEncryptedPassword("adsfasdfasdf");

        assertThatExceptionOfType(AuthorityNotFoundException.class)
                .isThrownBy(() -> {
                    underTest.getAuthorityByAppUser(appUser);
                }).withMessageContaining("AppUser");
    }

    @Test
    void checkIfUpdatesAuthorityByAppUser() {
        underTest.createAuthority(authority);

        underTest.updateAuthorityByAppUser(authority.getAppUser(), AuthorityType.PREMIUM);
        entityManager.refresh(authority);
        entityManager.flush();
        Optional<Authority> optional = authorityRepository.findById(authority.getId());

        assertThat(optional.get().getAuthority()).isEqualTo(AuthorityType.PREMIUM);
    }

    @Test
    void checkIfUpdateAuthorityByAppUserThrowsExceptionWhenChangeToAdmin() {
        underTest.createAuthority(authority);

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> {
                    underTest.updateAuthorityByAppUser(authority.getAppUser(), AuthorityType.ADMIN);
                }).withMessageContaining("ADMIN");
    }

    @Test
    void checkIfUpdateAuthorityByAppUserThrowsExceptionWhenAuthorityDoesNotExist() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> {
                    underTest.updateAuthorityByAppUser(authority.getAppUser(), AuthorityType.PREMIUM);
                }).withMessageContaining("AppUser");
    }
}