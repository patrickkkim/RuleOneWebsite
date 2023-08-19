package com.valueinvesting.ruleone.repositories;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.entities.Authority;
import com.valueinvesting.ruleone.entities.AuthorityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AuthorityRepositoryTest {

    @Autowired TestEntityManager testEntityManager;
    @Autowired AuthorityRepository underTest;
    AppUser appUser;

    @BeforeEach
    void setUp() {
        appUser = new AppUser();
        appUser.setUsername("honggildong");
        appUser.setEmail("a@a.com");
        appUser.setEncryptedPassword("asdfasdfasdf");
    }

    @Test
    void checkIfFindsByAppUserId() {
        Authority authority = new Authority();
        appUser.setAuthority(new HashSet<>(List.of(authority)));
        authority.setAppUser(appUser);
        authority.setAuthority(AuthorityType.ESSENTIAL);

        underTest.save(authority);
        Optional<Authority> optional = underTest.findByAppUserId(appUser.getId());

        assertThat(optional.isPresent()).isTrue();
        assertThat(optional.get()).isEqualTo(authority);
    }

    @Test
    void checkIfUpdatesAuthorityById() {
        Authority authority = new Authority();
        appUser.setAuthority(new HashSet<>(List.of(authority)));
        authority.setAppUser(appUser);
        authority.setAuthority(AuthorityType.ESSENTIAL);

        underTest.save(authority);
        underTest.updateAuthorityById(authority.getId(), AuthorityType.PREMIUM);
        testEntityManager.refresh(authority);
        Optional<Authority> optional = underTest.findById(authority.getId());

        assertThat(optional.get().getAuthority()).isEqualTo(AuthorityType.PREMIUM);
    }

    @Test
    void checkIfDoesNotUpdatesAuthorityByIdWhenAuthorityIsNotAllowed() {
        Authority authority = new Authority();
        appUser.setAuthority(new HashSet<>(List.of(authority)));
        authority.setAppUser(appUser);
        authority.setAuthority(AuthorityType.ESSENTIAL);

        underTest.save(authority);

        assertThatExceptionOfType(DataIntegrityViolationException.class)
                .isThrownBy(() -> {
                    underTest.updateAuthorityById(authority.getId(), null);
                }).withMessageContaining("NULL not allowed");
    }
}