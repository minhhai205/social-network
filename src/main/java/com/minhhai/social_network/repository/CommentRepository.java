package com.minhhai.social_network.repository;

import com.minhhai.social_network.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph(attributePaths = {
            "userCreated",
            "parentComment"
    })
    Page<Comment> findAll(Specification<Comment> spec, Pageable pageable);
}
