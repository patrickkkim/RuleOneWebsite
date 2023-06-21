package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.exceptions.UserAlreadyExistException;
import com.valueinvesting.ruleone.exceptions.UserNotFoundException;
import com.valueinvesting.ruleone.repositories.AppUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AppUserServiceImpl(AppUserRepository appUserRepository, BCryptPasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public AppUser createAppUser(AppUser appUser) {
        appUser.setEncryptedPassword(encodePassword(appUser.getEncryptedPassword()));

        Optional<AppUser> optionalById = appUserRepository.findById(appUser.getId());
        Optional<AppUser> optionalByUsername = appUserRepository.findByUsername(appUser.getUsername());
        if (optionalById.isPresent()) {
            throw new UserAlreadyExistException(
                    "User already exists with ID: " + appUser.getId());
        }
        else if (optionalByUsername.isPresent()) {
            throw new UserAlreadyExistException(
                    "User already exists with username: " + appUser.getUsername());
        }
        else {
            return appUserRepository.save(appUser);
        }
    }

    @Override
    public AppUser getAppUserById(int id) {
        Optional<AppUser> optional = appUserRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        else {
            throw new UserNotFoundException("User does not exist with ID: " + id);
        }
    }

    @Override
    public AppUser getAppUserByUsername(String username) {
        Optional<AppUser> optional = appUserRepository.findByUsername(username);
        if (optional.isPresent()) {
            return optional.get();
        }
        else {
            throw new UserNotFoundException("User does not exist with username: " + username);
        }
    }

    @Transactional
    @Override
    public void updateUser(int id, String password, String email) {
        Optional<AppUser> optional = appUserRepository.findById(id);
        if (optional.isPresent()) {
            AppUser appUser = optional.get();
            if (password != null) {
                appUserRepository.updatePasswordById(id, encodePassword(password));
            }
            if (email != null) {
                if (!email.equals(appUser.getEmail())) {
                    appUserRepository.updateEmailById(id, email);
                }
                else {
                    throw new RuntimeException("Email cannot be changed to the same email");
                }
            }
        }
        else {
            throw new UserNotFoundException("User does not exist with ID: " + id);
        }
    }

    @Override
    public void activateUser(int id) {
        Optional<AppUser> optional = appUserRepository.findById(id);
        if (optional.isPresent()) {
            AppUser appUser = optional.get();
            appUserRepository.updateActiveById(id, true);
        }
        else {
            throw new UserNotFoundException("User does not exist with ID: " + id);
        }
    }

    @Override
    public void deactivateUser(int id) {
        Optional<AppUser> optional = appUserRepository.findById(id);
        if (optional.isPresent()) {
            AppUser appUser = optional.get();
            appUserRepository.updateActiveById(id, false);
        }
        else {
            throw new UserNotFoundException("User does not exist with ID: " + id);
        }
    }

    @Override
    public void deleteUser(int id) {
        Optional<AppUser> optional = appUserRepository.findById(id);
        if (optional.isPresent()) {
            appUserRepository.deleteById(id);
        }
        else {
            throw new UserNotFoundException("User does not exist with ID: " + id);
        }
    }

    private String encodePassword(String plainText) {
        String password = passwordEncoder.encode(plainText);
        String encodeType = "{bcrypt}";
        return encodeType + password;
    }
}
