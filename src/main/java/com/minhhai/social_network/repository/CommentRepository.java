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
}
