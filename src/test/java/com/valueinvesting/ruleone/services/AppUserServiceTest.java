package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.exceptions.UserAlreadyExistException;
import com.valueinvesting.ruleone.exceptions.UserNotFoundException;
import com.valueinvesting.ruleone.repositories.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {

    @Mock private AppUserRepository appUserRepository;
    @Mock private BCryptPasswordEncoder bCryptPasswordEncoder;
    private AppUser appUser;
    private String encryptedPassword;
    private String encryptionType;
    private AppUserService underTest;

    @BeforeEach
    void setUp() {
        underTest = new AppUserServiceImpl(appUserRepository, bCryptPasswordEncoder);
        appUser = new AppUser();
        appUser.setUsername("honggilddong");
        appUser.setEmail("a@a.com");
        appUser.setEncryptedPassword("test123");
        encryptedPassword = "$2a$10$.aD/FvmNYxyasxwZqSjCqu6/WIdiAVAgtJvpNm3cTprbcpVJzTA1K";
        encryptionType = "{bcrypt}";
    }

    @Test
    void checkIfCreatesAppUser() {
        given(bCryptPasswordEncoder.encode(anyString()))
                .willReturn(encryptedPassword);
        given(appUserRepository.findById(anyInt()))
                .willReturn(Optional.empty());
        given(appUserRepository.findByUsername(anyString()))
                .willReturn(Optional.empty());
        given(appUserRepository.save(any(AppUser.class)))
                .willReturn(appUser);

        assertThat(underTest.createAppUser(appUser)).isEqualTo(appUser);
    }

    @Test
    void checkIfCreateAppUserInsertsValidPassword() {
        given(bCryptPasswordEncoder.encode(anyString()))
                .willReturn(encryptedPassword);
        given(appUserRepository.findById(anyInt()))
                .willReturn(Optional.empty());
        given(appUserRepository.findByUsername(anyString()))
                .willReturn(Optional.empty());
        given(appUserRepository.save(any(AppUser.class)))
                .willReturn(appUser);

        assertThat(underTest.createAppUser(appUser).getEncryptedPassword())
                .isEqualTo(encryptionType + encryptedPassword);
    }

    @Test
    void checkIfCreateAppUserThrowsErrorOnSameId() {
        given(bCryptPasswordEncoder.encode(anyString()))
                .willReturn(encryptedPassword);
        given(appUserRepository.findById(anyInt()))
                .willReturn(Optional.of(appUser));

        assertThatExceptionOfType(UserAlreadyExistException.class)
                .isThrownBy(() -> {
                    underTest.createAppUser(appUser);
                }).withMessageContaining("ID");
    }

    @Test
    void checkIfCreateAppUserThrowsExceptionOnSameUsername() {
        given(bCryptPasswordEncoder.encode(anyString()))
                .willReturn(encryptedPassword);
        given(appUserRepository.findByUsername(anyString()))
                .willReturn(Optional.of(appUser));

        assertThatExceptionOfType(UserAlreadyExistException.class)
                .isThrownBy(() -> {
                    underTest.createAppUser(appUser);
                }).withMessageContaining("username");
    }

    @Test
    void checkIfGetsAppUserById() {
        given(appUserRepository.findById(anyInt()))
                .willReturn(Optional.of(appUser));

        assertThat(underTest.getAppUserById(1)).isEqualTo(appUser);
    }

    @Test
    void checkIfGetAppUserByIdThrowsExceptionOnNoUserFound() {
        given(appUserRepository.findById(anyInt()))
                .willReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    underTest.getAppUserById(1);
                }).withMessageContaining("ID");
    }

    @Test
    void checkIfGetsAppUserByUsername() {
        given(appUserRepository.findByUsername(anyString()))
                .willReturn(Optional.of(appUser));

        assertThat(underTest.getAppUserByUsername("honggilddong")).isEqualTo(appUser);
    }

    @Test
    void checkIfGetAppUserByUsernameThrowsExceptionOnNoUserFound() {
        given(appUserRepository.findByUsername(anyString()))
                .willReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    underTest.getAppUserByUsername("honggilddong");
                }).withMessageContaining("username");
    }

    @Test
    void checkIfUpdatesUserPassword() {
        given(appUserRepository.findById(anyInt()))
                .willReturn(Optional.of(appUser));
        given(bCryptPasswordEncoder.encode(appUser.getEncryptedPassword()))
                .willReturn(encryptedPassword);

        underTest.updateUser(1, appUser.getEncryptedPassword(), null);
        ArgumentCaptor<String> appUserPasswordCaptor =
            ArgumentCaptor.forClass(String.class);
        verify(appUserRepository)
                .updatePasswordById(anyInt(), appUserPasswordCaptor.capture());
        String capturedPassword = appUserPasswordCaptor.getValue();

        assertThat(capturedPassword).isEqualTo(encryptionType + encryptedPassword);
    }

    @Test
    void checkIfUpdatesUserEmail() {
        given(appUserRepository.findById(anyInt()))
                .willReturn(Optional.of(appUser));

        underTest.updateUser(1, null, "b@b.com");
        ArgumentCaptor<String> appUserEmailCaptor =
                ArgumentCaptor.forClass(String.class);
        verify(appUserRepository)
                .updateEmailById(anyInt(), appUserEmailCaptor.capture());
        String capturedEmail = appUserEmailCaptor.getValue();

        assertThat(capturedEmail).isEqualTo("b@b.com");
    }

    @Test
    void checkIfUpdateUserThrowsExceptionWhenSameEmailIsInserted() {
        given(appUserRepository.findById(anyInt()))
                .willReturn(Optional.of(appUser));

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> {
                    underTest.updateUser(1, null, appUser.getEmail());
                })
                .withMessageContaining("email");
    }

    @Test
    void activateUser() {
        given(appUserRepository.findById(1))
                .willReturn(Optional.of(appUser));

        underTest.activateUser(1);
        ArgumentCaptor<Boolean> appUserActiveCaptor =
                ArgumentCaptor.forClass(Boolean.class);
        verify(appUserRepository)
                .updateActiveById(anyInt(), appUserActiveCaptor.capture());

        assertThat(appUserActiveCaptor.getValue()).isTrue();
    }

    @Test
    void deactivateUser() {
        given(appUserRepository.findById(1))
                .willReturn(Optional.of(appUser));

        underTest.deactivateUser(1);
        ArgumentCaptor<Boolean> appUserActiveCaptor =
                ArgumentCaptor.forClass(Boolean.class);
        verify(appUserRepository)
                .updateActiveById(anyInt(), appUserActiveCaptor.capture());

        assertThat(appUserActiveCaptor.getValue()).isFalse();
    }

    @Test
    @Disabled
    void deleteUser() {
    }
}