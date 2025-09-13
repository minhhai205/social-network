package com.minhhai.social_network.repository;

import com.minhhai.social_network.entity.Conversation;
import com.minhhai.social_network.entity.Message;
import com.minhhai.social_network.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findAll(Specification<Conversation> spec, Pageable pageable);

    @EntityGraph(attributePaths = {
            "userSent",
            "messageMedia",
    })
    List<Message> findByIdIn(List<Long> messageIds);
}
