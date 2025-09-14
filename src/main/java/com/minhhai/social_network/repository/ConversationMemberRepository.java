package com.minhhai.social_network.repository;

import com.minhhai.social_network.entity.ConversationMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationMemberRepository extends JpaRepository<ConversationMember, Long> {
    @Query("""
        SELECT cm FROM ConversationMember cm
        WHERE cm.user.username = :username
            AND cm.user.deleted = false
            AND cm.conversation.id = :conversationId
            AND cm.deleted = false
    """)
    Optional<ConversationMember> findMemberByUsername(@Param("username") String username,
                                                      @Param("conversationId") Long conversationId);

    @Query("""
        SELECT cm FROM ConversationMember cm
        JOIN FETCH cm.user u
        WHERE u.id = :memberId
            AND u.deleted = false
            AND cm.conversation.id = :conversationId
            AND cm.deleted = true
    """)
    Optional<ConversationMember> findOldMemberById(@Param("memberId") Long memberId,
                                                   @Param("conversationId") Long conversationId);
}
