package com.mountblue.blogapplication.repository;

import com.mountblue.blogapplication.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {
    // Add custom query methods if needed
    Tag findByName(String name);
    Set<Tag> findByPostsId(Long postId);
}