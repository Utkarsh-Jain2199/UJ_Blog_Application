package com.mountblue.blogapplication.repository;

import com.mountblue.blogapplication.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface PostRepository extends JpaRepository<Post, Long> {
    // Add custom query methods if needed
    @Query("SELECT p FROM Post p " +
            "LEFT JOIN p.tags t " +
            "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(p.author) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ")
    Page<Post> searchPosts(@Param("searchTerm") String searchTerm, Pageable pageable);
    List<Post> findByAuthorInAndTagsIdIn(Set<String> authorNames, Set<Long> tagIds);
    List<Post> findByAuthorIn(Set<String> authors);
    List<Post> findByTagsIdIn(Set<Long> tags);
    @Query("SELECT DISTINCT p.author FROM Post p")
    Set<String> findAllAuthors();

}
