package com.mountblue.blogapplication.controller;

import com.mountblue.blogapplication.entities.Comment;
import com.mountblue.blogapplication.entities.Post;
import com.mountblue.blogapplication.entities.Tag;
import com.mountblue.blogapplication.entities.User;
import com.mountblue.blogapplication.repository.TagRepository;
import com.mountblue.blogapplication.repository.UserRepository;
import com.mountblue.blogapplication.service.CommentService;
import com.mountblue.blogapplication.service.PostService;
import com.mountblue.blogapplication.service.TagService;
import com.mountblue.blogapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final TagService tagService;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;


    @Autowired
    public PostController(PostService postService, CommentService commentService, TagService tagService,
                          TagRepository tagRepository, UserRepository userRepository) {
        this.postService = postService;
        this.commentService = commentService;
        this.tagService = tagService;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;

    }

    @GetMapping("/newPost")
    public String createPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "createPost";
    }

    @PostMapping("/create")
    public String addPostToDataBase(@ModelAttribute("post") Post post, @RequestParam("tagsInput") String tagsInput) {
        //post.setAuthor("Unknown");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        if (user.getRole().equals("author") || user.getRole().equals("admin")) {
            post.setAuthor(user.getName());
        }
        user.getPosts().add(post);
        userRepository.save(user);
        post.setCreatedAt(LocalDateTime.now());
        post.setPublishedAt(LocalDateTime.now());
        postService.createPost(post);
        tagService.addTagsToPost(post.getId(), tagsInput);
        return "redirect:/";
        // return "createPost";
    }


    @GetMapping("/filter")
    public String filterPosts(@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(required = false) Set<String> authors,
                              @RequestParam(required = false) Set<Long> tags,
                              Model model) {
        int actualPageNo = (pageNo != null) ? pageNo : 0;
        int pageSize = 4;

        List<Post> filteredPosts = postService.filterPosts(authors, tags);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("posts", filteredPosts);
        model.addAttribute("authors", postService.getAllAuthors());
        model.addAttribute("tags", tagRepository.findAll());
        model.addAttribute("currentPage", actualPageNo);
        model.addAttribute("totalPages", 1);
        model.addAttribute("sortField", "createdAt");
        model.addAttribute("sortOrder", "asc");
        // Persist selected author and tags for dropdowns
        model.addAttribute("selectedAuthor", (authors != null && !authors.isEmpty()) ? authors.iterator().next() : "");
        model.addAttribute("selectedTags", tags);
        return "homepage";
    }

    @GetMapping("/")
    public String showFrontPage(@RequestParam(defaultValue = "0") Integer pageNo,
                                @RequestParam(defaultValue = "createdAt") String sortField,
                                @RequestParam(defaultValue = "desc") String sortOrder,
                                @RequestParam(required = false) String searchTerm,
                                Model model) {
        int actualPageNo = (pageNo != null) ? pageNo : 0;
        int pageSize = 6;
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortField);
        Page<Post> postPage;
        PageRequest pageRequest = PageRequest.of(actualPageNo, pageSize, sort);
        if (searchTerm != null && !searchTerm.isEmpty()) {
            postPage = postService.searchPosts(searchTerm, pageRequest);
        } else {
            postPage = postService.getAllPosts(pageRequest);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("authors", postService.getAllAuthors());
        model.addAttribute("tags", tagRepository.findAll());
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", actualPageNo);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortOrder", sortOrder);
        return "homepage";
    }

    @GetMapping("/posts/{postId}")
    public String showSinglePost(@PathVariable Long postId, Model model) {
        List<Comment> commentList = commentService.getCommentsByPostId(postId);
        model.addAttribute("commentsList", commentList);
        Post post = postService.getPostById(postId);
        model.addAttribute("post", post);
        Set<Tag> tags = post.getTags();
        model.addAttribute("tags", tags);
        return "post";
    }

    @PostMapping("/delete/{postId}")
    public String deletePost(@PathVariable Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        Post post = postService.getPostById(postId);
        if (user.getRole().equals("author")) {

            if (!user.getPosts().contains(post)) {
                return "access-denied";
            }

        }
        user.getPosts().remove(post);
        userRepository.save(user);
        postService.deletePost(postId);
        return "redirect:/";
    }

    @GetMapping("/update/{postId}")
    public String showUpdateForm(@PathVariable Long postId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        Post post = postService.getPostById(postId);
        if (user.getRole().equals("author")) {
            if (!user.getPosts().contains(post)) {
                return "access-denied";
            }
        }
        Set<Tag> existingTags = tagService.getTagsByPostId(postId);
        ArrayList<String> listOfExistingTags = new ArrayList<>();
        for (Tag tag : existingTags) {
            listOfExistingTags.add(tag.getName());
        }
        String commaSeperatedTags = String.join(",", listOfExistingTags);
        model.addAttribute("tags", commaSeperatedTags);
        model.addAttribute("post", post);
        return "updatePost";
    }

    @PostMapping("/update/{postId}")
    public String updatePost(@PathVariable Long postId, @ModelAttribute Post updatedPost,
                             @RequestParam("tag") String updatedTags) {
        updatedPost.setUpdatedAt(LocalDateTime.now());
        Post post = postService.getPostById(postId);
        updatedPost.setAuthor(post.getAuthor());
        updatedPost.setCreatedAt(LocalDateTime.now());
        updatedPost.setId(postId);
        postService.updatePost(updatedPost);
        tagService.addTagsToPost(postId, updatedTags);
        return "redirect:/posts/" + postId;

    }
}
