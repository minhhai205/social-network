package com.minhhai.social_network.repository;

import com.minhhai.social_network.entity.JoinGroupRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JoinGroupRequestRepository extends JpaRepository<JoinGroupRequest, Long> {

    @EntityGraph(attributePaths = {
            "createdBy"
    })
    @Query("SELECT r FROM JoinGroupRequest r WHERE r.group.id=:groupId")
    List<JoinGroupRequest> findAllByGroupId(@Param("groupId") Long groupId);

    @EntityGraph(attributePaths = {
            "createdBy",
            "group"
    })
    @Query("SELECT r FROM JoinGroupRequest r WHERE r.id=:requestId")
    Optional<JoinGroupRequest> findByIdWithGroupAndUserCreated(@Param("requestId") Long requestId);
}
