package com.github.maximslepukhin.repository;

import com.github.maximslepukhin.model.Comment;
import com.github.maximslepukhin.model.Post;

import java.util.List;

public interface CommentRepository {

    void addComment(Long postId, String text);

    void editCommentOfPost(Post post, Long commentId, String text);

    void deleteCommentOfPost(Long postId, Long commentId);

    List<Comment> getCommentsOfPost(Post post);
}
