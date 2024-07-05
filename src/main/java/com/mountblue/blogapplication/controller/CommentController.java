package com.mountblue.blogapplication.controller;

import com.mountblue.blogapplication.entities.Comment;
import com.mountblue.blogapplication.entities.Post;
import com.mountblue.blogapplication.entities.User;
import com.mountblue.blogapplication.repository.UserRepository;
import com.mountblue.blogapplication.service.CommentService;
import com.mountblue.blogapplication.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class CommentController {
    private final CommentService commentService;
    private final PostService postService;

    private final UserRepository userRepository;

    @Autowired
    public CommentController(CommentService commentService, PostService postService, UserRepository userRepository) {
        this.commentService = commentService;
        this.postService = postService;
        this.userRepository = userRepository;
    }

    @PostMapping("/comment/{postId}")
    public String addNewComment(@PathVariable Long postId, @ModelAttribute("newComment") Comment comment) {
        Post post = postService.getPostById(postId);
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());
        commentService.addComment(comment);

        return "redirect:/posts/" + postId;
    }

    @PostMapping("/deleteComment/{commentId}")
    public String deleteComment(@PathVariable Long commentId, @RequestParam Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        Post post = postService.getPostById(commentId);
        if (user.getRole().equals("author")) {
            if (!user.getPosts().contains(post)) {
                return "access-denied";
            }

        }
        commentService.deleteComment(commentId);
        // Redirect back to the post details page after deleting the comment
        return "redirect:/posts/" + postId;
    }

    @GetMapping("/editComment/{commentId}")
    public String showEditCommentForm(@PathVariable Long commentId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        Post post = postService.getPostById(commentId);
        if (user.getRole().equals("author")) {
            if (!user.getPosts().contains(post)) {
                return "access-denied";
            }

        }
        Comment comment = commentService.getCommentById(commentId);
        model.addAttribute("comment", comment);
        return "editComment";
    }

    @PostMapping("/comments/{commentId}")
    public String updateComment(@PathVariable Long commentId, @ModelAttribute("existingComment") Comment comment) {
        String updated = comment.getComment();
        commentService.editComment(commentId, updated);
        Comment commentUpdate = commentService.getCommentById(commentId);
        Long postId = commentUpdate.getPost().getId();
        return "redirect:/posts/" + postId;
    }
}
