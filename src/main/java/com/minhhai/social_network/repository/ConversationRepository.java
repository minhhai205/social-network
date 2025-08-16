package com.minhhai.social_network.repository;

import com.minhhai.social_network.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query("""
            SELECT c FROM Conversation c
            JOIN FETCH c.conversationMember cm
            JOIN FETCH cm.user u
            WHERE c.id=:id
    """)
    Optional<Conversation> findByIdWithUserCreatedAndAllMember(@Param("id") Long id);

    @Query("""
            SELECT c FROM Conversation c
            JOIN FETCH c.conversationMember cm
            JOIN FETCH cm.user u
            WHERE c.id=:id
    """)
    Optional<Conversation> findByIdWithAllMember(@Param("id") Long id);

    @Query("""
        SELECT c FROM Conversation c
        JOIN c.conversationMember m1
        JOIN c.conversationMember m2
        WHERE c.isGroup = false
        AND m1.user.id = :userId1
        AND m2.user.id = :userId2
    """)
    Optional<Conversation> findOneToOneConversation(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
