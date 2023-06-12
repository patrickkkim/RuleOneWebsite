package com.valueinvesting.ruleone.repositories;

import com.valueinvesting.ruleone.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
    Optional<AppUser> findByUsername(String username);

    @Modifying
    @Query("UPDATE AppUser u SET u.encryptedPassword = :password WHERE u.id = :id")
    void updatePasswordById(@Param("id") int id, @Param("password") String password);

    @Modifying
    @Query("UPDATE AppUser u SET u.email = :email WHERE u.id = :id")
    void updateEmailById(@Param("id") int id, @Param("email") String email);

    @Modifying
    @Query("UPDATE AppUser u SET u.isActive = :isActive WHERE u.id = :id")
    void updateActiveById(@Param("id") int id, @Param("isActive") boolean isActive);
}
