package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.entities.Authority;
import com.valueinvesting.ruleone.exceptions.UserAlreadyExistException;
import com.valueinvesting.ruleone.exceptions.UserNotFoundException;
import com.valueinvesting.ruleone.repositories.AppUserRepository;
import com.valueinvesting.ruleone.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public AppUserServiceImpl(AppUserRepository appUserRepository, BCryptPasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    @Override
    public AppUser createAppUser(AppUser appUser) {
        appUser.setEncryptedPassword(encodePassword(appUser.getEncryptedPassword()));
        Authority authority = new Authority();
        authority.setAppUser(appUser);
        appUser.setAuthority(new HashSet<>(List.of(authority)));

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

    @Transactional
    @Override
    public void updateUserAuthority(int id, Set<Authority> authorities) {
        AppUser appUser = this.getAppUserById(id);
        if (authorities.size() == 0) throw new IllegalArgumentException("Authority set must not be empty");
        appUser.setAuthority(authorities);
        appUserRepository.save(appUser);
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

    @Override
    public void deleteAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username;
        if (authentication != null && authentication.isAuthenticated()) {
            username = authentication.getName();
        }
        else throw new BadCredentialsException("User is not authenticated");

        AppUser appUser = this.getAppUserByUsername(username);
        deleteUser(appUser.getId());
    }

    private String encodePassword(String plainText) {
        String password = passwordEncoder.encode(plainText);
        String encodeType = "{bcrypt}";
        return encodeType + password;
    }

    @Override
    public String login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        if (authentication.isAuthenticated()) {
            return jwtUtil.generateToken(username);
        }
        else throw new BadCredentialsException("Login failed: Bad credentials");
    }
}
