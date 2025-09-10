package com.minhhai.social_network.repository;

import com.minhhai.social_network.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

//    @EntityGraph(attributePaths = {
//            "userCreated",
//            "group",
//            "postMedia"
//    })
    Page<Post> findAll(Specification<Post> spec, Pageable pageable);

    @EntityGraph(attributePaths = {
            "userCreated",
            "group",
            "postMedia"
    })
    List<Post> findByIdIn(List<Long> postIds);

    @Query("""
        SELECT p.id, COUNT(r.id)
        FROM Post p
        LEFT JOIN p.reactions r
        WHERE p.id IN :postIds
        GROUP BY p.id
    """)
    List<Object[]> findCountReactionForPosts(@Param("postIds") List<Long> postIds);

    @Query("""
        SELECT p.id, COUNT(c.id)
        FROM Post p
        LEFT JOIN p.comments c
        WHERE p.id IN :postIds
        GROUP BY p.id
    """)
    List<Object[]> findCountCommentForPosts(@Param("postIds") List<Long> postIds);

    @Query("""
        SELECT p FROM Post p
        JOIN FETCH p.userCreated u
        LEFT JOIN FETCH p.postMedia m
        WHERE p.group.id=:groupId
        AND p.status = 'PENDING'
    """)
    List<Post> findAllCreatePostRequestByGroupId(@Param("groupId") Long groupId);

    @Query("""
        SELECT p FROM Post p
        JOIN FETCH p.userCreated u
        JOIN FETCH p.group g
        WHERE p.id =:postId
        AND p.status = 'PENDING'
    """)
    Optional<Post> findRequestByIdWithGroupAndUserCreated(@Param("postId") Long postId);

    @Query("""
        SELECT p FROM Post p
        JOIN FETCH p.userCreated u
        JOIN FETCH p.group g
        LEFT JOIN FETCH p.postMedia m
        WHERE p.id =:postId
        AND (p.status = 'PENDING' OR p.status = 'APPROVED')
    """)
    Optional<Post> findByIdWithGroupAndUserCreated(@Param("postId") Long postId);

    @Query("""
        SELECT p FROM Post p
        JOIN FETCH p.userCreated u
        LEFT JOIN FETCH p.group g
        WHERE p.id =:postId
        AND p.status = 'APPROVED'
    """)
    Optional<Post> findPostActiveByIdWithGroupAndUserCreated(@Param("postId") Long postId);
}
