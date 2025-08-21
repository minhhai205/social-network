package com.minhhai.social_network.repository;

import com.minhhai.social_network.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    @EntityGraph(attributePaths = {
            "roles",
            "roles.permissions",
    })
    @Query("SELECT u FROM User u WHERE u.username=:username AND u.deleted=false")
    Optional<User> findByUsername(@Param("username") String username);

    @EntityGraph(attributePaths = {
            "roles",
            "roles.permissions",
    })
    @Query("SELECT u FROM User u WHERE u.id=:id AND u.deleted=false")
    Optional<User> findById(@Param("id") Long id);

    @EntityGraph(attributePaths = {
            "roles",
            "roles.permissions",
    })
    @Query("SELECT u FROM User u WHERE u.id IN :userIds AND u.deleted=false")
    List<User> findByIdIn(@Param("userIds") Collection<Long> userIds);

    @EntityGraph(attributePaths = {
            "roles",
            "roles.permissions",
    })
    @Query("SELECT u FROM User u WHERE u.email=:email AND u.deleted=false")
    Optional<User> findByEmail(@Param("email") String email);

    @EntityGraph(attributePaths = {
            "roles",
            "roles.permissions",
    })
    @Query("SELECT u FROM User u WHERE u.providerId=:providerId AND u.deleted=false")
    Optional<User> findByProviderId(@Param("providerId") String providerId);
}