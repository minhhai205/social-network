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
        AND m.status = 'ACTIVE'
    """)
    Optional<GroupMember> findMemberByMemberId(@Param("memberId") Long memberId, @Param("groupId") Long groupId);

    @Query("""
        SELECT m FROM GroupMember m
        WHERE m.user.id=:memberId
        AND m.group.id=:groupId
        AND (m.role = 'ADMIN' OR m.role = 'MODERATOR')
        AND m.status = 'ACTIVE'
    """)
    Optional<GroupMember> findAdminOrModeratorById(@Param("memberId") Long memberId, @Param("groupId") Long groupId);

    @Query("""
        SELECT m FROM GroupMember m
        WHERE m.user.id=:memberId
        AND m.group.id=:groupId
        AND m.role = 'ADMIN'
        AND m.status = 'ACTIVE'
    """)
    Optional<GroupMember> findAdminById(@Param("memberId") Long memberId, @Param("groupId") Long groupId);
}
