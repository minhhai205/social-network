package com.minhhai.social_network.repository;

import com.minhhai.social_network.entity.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Page<Conversation> findAll(Specification<Conversation> spec, Pageable pageable);

    @Query("""
            SELECT c FROM Conversation c
            JOIN FETCH c.conversationMember cm
            JOIN FETCH cm.user u
            WHERE c.id=:id
            AND c.deleted=false
            AND cm.deleted=false
    """)
    Optional<Conversation> findByIdWithAllMember(@Param("id") Long id);

    @Query("""
        SELECT c FROM Conversation c
        JOIN c.conversationMember m1
        JOIN c.conversationMember m2
        WHERE c.isGroup = false
        AND m1.user.id = :userId1
        AND m2.user.id = :userId2
        AND c.deleted=false
        AND m1.deleted=false
        AND m2.deleted=false
    """)
    Optional<Conversation> findOneToOneConversation(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
