package com.example.usedtrade.domain.user.repository;

import com.example.usedtrade.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @EntityGraph(attributePaths = "roleSet")
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = "roleSet")
    @Query("select u from User u where u.username = :username and u.social = false")
    Optional<User> getWithRoles(String username);
}
