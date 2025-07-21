package com.minhhai.social_network.repository;

import com.minhhai.social_network.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    @EntityGraph(attributePaths = {
            "roles",
            "roles.permissions",
    })
    @Query("SELECT u FROM User u WHERE u.username=:username")
    Optional<User> findByUsername(@Param("username") String username);

    @EntityGraph(attributePaths = {
            "roles",
            "roles.permissions",
    })
    @Query("SELECT u FROM User u WHERE u.email=:email")
    Optional<User> findByEmail(@Param("email") String email);

    @EntityGraph(attributePaths = {
            "roles",
            "roles.permissions",
    })
    @Query("SELECT u FROM User u WHERE u.providerId=:providerId")
    Optional<User> findByProviderId(@Param("providerId") String providerId);
}