package com.minhhai.social_network.repository;

import com.minhhai.social_network.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    @Query("""
        SELECT m FROM GroupMember m
        WHERE m.user.id=:memberId
        AND m.group.id=:groupId
    """)
    Optional<GroupMember> findMemberByMemberId(@Param("memberId") Long memberId, @Param("groupId") Long groupId);
}
