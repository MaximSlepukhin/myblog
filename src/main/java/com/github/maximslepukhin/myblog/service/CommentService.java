package com.github.maximslepukhin.myblog.service;

import com.github.maximslepukhin.myblog.model.Post;

public interface CommentService {

    void addComment(Long postId, String text);

    void editCommentOfPost(Post post, Long commentId, String text);

    void deleteCommentOfPost(Long postId, Long commentId);
}
