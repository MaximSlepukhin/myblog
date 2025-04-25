package com.github.maximslepukhin.service;

import com.github.maximslepukhin.model.Post;

public interface CommetService {

    void addComment(Long postId, String text);

    void editCommentOfPost(Post post, Long commentId, String text);

    void deleteCommentOfPost(Long postId, Long commentId);
}
