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
    """)
    Optional<ConversationMember> findMemberByUsername(@Param("username") String username,
                                            @Param("conversationId") Long conversationId);
}
