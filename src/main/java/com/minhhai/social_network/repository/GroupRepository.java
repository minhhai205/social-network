package com.minhhai.social_network.repository;

import com.minhhai.social_network.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    @Query("""
        SELECT gr FROM Group gr
        JOIN FETCH gr.groupMembers gm
        JOIN FETCH gm.user u
        WHERE gr.deleted = false
    """)
    Optional<Group> findByIdWithAndAllMember(@Param("groupId") Long groupId);
}
