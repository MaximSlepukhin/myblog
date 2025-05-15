package com.github.maximslepukhin.myblog.controller;

import com.github.maximslepukhin.myblog.dto.PostDto;
import com.github.maximslepukhin.myblog.mapper.PostMapper;
import com.github.maximslepukhin.myblog.model.Post;
import com.github.maximslepukhin.myblog.service.CommentService;
import com.github.maximslepukhin.myblog.service.PostService;
import com.github.maximslepukhin.myblog.model.PostsResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import java.util.stream.Collectors;


@Controller
@Slf4j
public class HomeController {

    private final PostService postService;
    private final CommentService commentService;

    public HomeController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @GetMapping("/")
    public String redirectToPosts() {
        return "redirect:/posts";
    }

    @GetMapping("/posts")
    public String getPosts(@RequestParam(defaultValue = "", name = "search") String search,
                           @RequestParam(defaultValue = "10", name = "pageSize") int pageSize,
                           @RequestParam(defaultValue = "1", name = "pageNumber") int page,
                           Model model) {
        log.info("/posts new request" + search);
        PostsResult postsResult = postService.findPosts(pageSize, page);

        List<PostDto> posts = postsResult.getPosts().stream()
                .map(PostMapper::toPostDto)
                .collect(Collectors.toList());

        model.addAttribute("search", search);
        model.addAttribute("paging", postsResult.getPageInfo());
        model.addAttribute("posts", posts);
        return "posts";
    }

    @GetMapping("/posts/{id}")
    public String getPostById(
            @PathVariable("id") Long id, Model model) {
        PostDto post = postService.findById(id);
        model.addAttribute("post", post);
        return "post";
    }

    @GetMapping("/posts/add")
    public String showPageWithAddingPost() {
        return "add-post";
    }

    @PostMapping("/posts")
    public String addPost(@ModelAttribute Post post,
                          @RequestPart(required = false, name = "image") MultipartFile image,
                          HttpServletRequest request) {
        Post savedPost = postService.savePost(post);
        Long postId = savedPost.getId();
        postService.saveImage(image, request, postId);

        return "redirect:/posts/" + postId;
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> showImage(@PathVariable("id") Long id, HttpServletRequest request) {
        return postService.getImageById(id, request);
    }

    @PostMapping("/posts/{id}/like")
    public String addLike(@PathVariable("id") Long id,
                          @RequestParam(name = "like") boolean like) {
        postService.addLike(id, like);
        return "redirect:/posts/" + id;
    }

    @GetMapping("/posts/{id}/edit")
    public String showEditPostForm(@PathVariable("id") Long id, Model model) {
        PostDto post = postService.findById(id);
        model.addAttribute("post", post);
        return "add-post";
    }

    @PostMapping("/posts/{id}")
    public String updatePost(@ModelAttribute Post post,
                             @PathVariable("id") Long id, @RequestParam(name = "image") MultipartFile image,
                             HttpServletRequest request) {
        postService.saveImage(image, request, post.getId());
        postService.updatePost(post);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/comments")
    public String addComment(@PathVariable("id") Long id, @RequestParam(name = "text") String text) {
        commentService.addComment(id, text);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/comments/{commentId}")
    public String editComment(@PathVariable(value = "id") Long id,
                              @PathVariable(value = "commentId") Long commentId,
                              @RequestParam(name = "text") String text) {
        Post post = postService.findPostById(id);
        commentService.editCommentOfPost(post, commentId, text);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable("id") Long id,
                                @PathVariable(value = "commentId") Long commentId) {
        commentService.deleteCommentOfPost(id, commentId);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePostById(@PathVariable("id") Long id, HttpServletRequest request) {
        postService.deletePostById(id, request);
        return "redirect:/posts";
    }
}