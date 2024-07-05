package com.mountblue.blogapplication.repository;

import com.mountblue.blogapplication.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Add custom query methods if needed
    List<Comment> findByPostId(Long postId);

}