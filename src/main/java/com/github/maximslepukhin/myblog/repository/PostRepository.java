package com.github.maximslepukhin.repository;

import com.github.maximslepukhin.model.Post;

import java.util.List;

public interface PostRepository {

    List<Post> findPosts(int pageSize, int page);

    Post findById(Long postId);

    Post savePost(Post post);

    void addLike(Long postId, boolean like);

    void updatePost(Post post);

    void deletePostById(Long postId);
}
