package com.valueinvesting.ruleone.repositories;

import com.valueinvesting.ruleone.entities.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
    Optional<Authority> findByAppUserId(int app_user_id);

    @Modifying
    @Query("UPDATE Authority a SET a.authority = :auth WHERE a.id = :id")
    void updateAuthorityById(@Param("id") int id, @Param("auth") String authority);
}
