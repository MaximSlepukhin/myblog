package com.github.maximslepukhin.myblog.repository;

import com.github.maximslepukhin.myblog.model.Post;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface PostRepository {

    List<Post> findPosts(int pageSize, int page);

    Post findById(Long postId);

    Post savePost(Post post);

    void addLike(Long postId, boolean like);

    void updatePost(Post post);

    void deletePostById(Long postId);
}
