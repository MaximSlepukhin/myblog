package com.github.maximslepukhin.myblog.service;

import com.github.maximslepukhin.myblog.model.Post;
import com.github.maximslepukhin.myblog.repository.CommentRepository;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public void addComment(Long postId, String text) {
        commentRepository.addComment(postId, text);

    }

    @Override
    public void editCommentOfPost(Post post, Long commentId, String text) {
        commentRepository.editCommentOfPost(post, commentId, text);
    }

    @Override
    public void deleteCommentOfPost(Long postId, Long commentId) {
        commentRepository.deleteCommentOfPost(postId, commentId);
    }
}
