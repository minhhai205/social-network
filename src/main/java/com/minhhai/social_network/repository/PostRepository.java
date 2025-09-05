package com.minhhai.social_network.repository;

import com.minhhai.social_network.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("""
        SELECT p FROM Post p
        JOIN FETCH p.userCreated u
        JOIN FETCH p.postMedia m
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
        JOIN FETCH p.postMedia m
        WHERE p.id =:postId
        AND (p.status = 'PENDING' OR p.status = 'APPROVED')
    """)
    Optional<Post> findByIdWithGroupAndUserCreated(@Param("postId") Long postId);
}
