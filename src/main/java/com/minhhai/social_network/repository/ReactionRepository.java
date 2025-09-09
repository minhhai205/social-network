package com.minhhai.social_network.repository;

import com.minhhai.social_network.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Integer> {
    @Query("""
        SELECT r FROM Reaction r
        WHERE r.userReaction.id = :userId
        AND r.post.id = :postId
    """)
    Optional<Reaction> findByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);

    @Query("""
        SELECT r FROM Reaction r
        WHERE r.userReaction.id = :userId
        AND r.comment.id = :commentId
    """)
    Optional<Reaction> findByUserIdAndCommentId(@Param("userId") Long userId, @Param("commentId") Long commentId);

}
