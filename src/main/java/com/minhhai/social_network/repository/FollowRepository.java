package com.minhhai.social_network.repository;

import com.minhhai.social_network.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    @Query("""
        SELECT f FROM Follow f
        WHERE f.sender.id = :fromId
        AND f.receiver.id = :toId
    """)
    Optional<Follow> findExistedFollow(@Param("fromId") Long fromId, @Param("toId") Long toId);
}
