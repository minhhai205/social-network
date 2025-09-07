package com.minhhai.social_network.repository;

import com.minhhai.social_network.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph(attributePaths = {
            "userCreated",
            "parentComment"
    })
    Page<Comment> findAll(Specification<Comment> spec, Pageable pageable);

    @Query("""
        SELECT c.id, COUNT(cl.id)
        FROM Comment c
        LEFT JOIN c.commentLikes cl
        WHERE c.id IN :commentIds
        GROUP BY c.id
    """)
    List<Object[]> findLikeCountForComments(@Param("commentIds") List<Long> commentIds);

    @Query("""
        SELECT c FROM Comment c
        LEFT JOIN FETCH c.userCreated u
        LEFT JOIN FETCH c.parentComment pc
        LEFT JOIN FETCH c.post p
        WHERE c.id=:commentId
    """)
    Optional<Comment> findByIdWithAllDetailAndGroup(@Param("commentId") Long commentId);
}
