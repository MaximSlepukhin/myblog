package com.github.maximslepukhin.myblog.repository;

import com.github.maximslepukhin.myblog.model.Comment;
import com.github.maximslepukhin.myblog.model.Post;

import java.util.List;

public interface CommentRepository {

    void addComment(Long postId, String text);

    void editCommentOfPost(Post post, Long commentId, String text);

    void deleteCommentOfPost(Long postId, Long commentId);

    List<Comment> getCommentsOfPost(Post post);
}
