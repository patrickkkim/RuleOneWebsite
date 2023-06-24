package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.entities.Authority;
import com.valueinvesting.ruleone.entities.AuthorityType;
import com.valueinvesting.ruleone.exceptions.AuthorityAlreadyExistException;
import com.valueinvesting.ruleone.exceptions.AuthorityNotFoundException;
import com.valueinvesting.ruleone.repositories.AuthorityRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;

    @Autowired
    public AuthorityServiceImpl(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @Transactional
    @Override
    public Authority createAuthority(Authority authority) {
        Optional<Authority> optional = authorityRepository.findById(authority.getId());
        if (optional.isPresent()) {
            throw new AuthorityAlreadyExistException(
                    "Authority already exists with ID:" + authority.getId());
        }
        else {
            return authorityRepository.save(authority);
        }
    }

    @Override
    public Authority getAuthorityById(int id) {
        Optional<Authority> optional = authorityRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        else {
            throw new AuthorityNotFoundException(
                    "Authority not found with ID:" + id);
        }
    }

    @Override
    public Authority getAuthorityByAppUser(AppUser appUser) {
        Optional<Authority> optional = authorityRepository.findByAppUserId(appUser.getId());
        if (optional.isPresent()) {
            return optional.get();
        }
        else {
            throw new AuthorityNotFoundException(
                    "Authority not found with AppUser: " + appUser.getUsername());
        }
    }

    @Transactional
    @Override
    public void updateAuthorityByAppUser(AppUser appUser, AuthorityType authority) {
        if (authority == AuthorityType.ADMIN) {
            throw new RuntimeException("Authority ADMIN type is not allowed to be changed");
        }
        Optional<Authority> optional = authorityRepository.findByAppUserId(appUser.getId());
        if (optional.isPresent()) {
            Authority authorityObject = optional.get();
            authorityRepository.updateAuthorityById(authorityObject.getId(), authority);
        }
        else {
            throw new AuthorityNotFoundException(
                    "Authority not found with AppUser: " + appUser.getUsername());
        }
    }
}
