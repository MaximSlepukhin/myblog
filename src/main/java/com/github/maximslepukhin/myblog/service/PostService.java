package com.github.maximslepukhin.service;

import com.github.maximslepukhin.dto.PostDto;
import com.github.maximslepukhin.model.Post;
import com.github.maximslepukhin.model.PostsResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {


    Post findPostById(Long postId);

    PostsResult findPosts(int pageSize, int page);

    PostDto findById(Long postId);

    void saveImage(MultipartFile image, HttpServletRequest request, Long postId);

    Post savePost(Post post);

    ResponseEntity<byte[]> getImageById(Long postId, HttpServletRequest request);

    void addLike(Long postId, boolean like);

    void updatePost(Post post);

    void deletePostById(Long postId, HttpServletRequest request);
}
