package com.github.maximslepukhin.service;

import com.github.maximslepukhin.model.Post;
import com.github.maximslepukhin.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl commentService;


    @Test
    void shouldAddComment() {
        commentService.addComment(1L,"Comment");
        verify(commentRepository, times(1)).addComment(1L, "Comment");
    }

    @Test
    void shouldEditCommentOfPost() {
        Post post = new Post();
        commentService.editCommentOfPost(post, 1L, "Comment");
        verify(commentRepository, times(1)).editCommentOfPost(post, 1L, "Comment");
    }

    @Test
    void shouldDeleteCommentOfPost() {
        commentService.deleteCommentOfPost(1L, 1L);
        verify(commentRepository, times(1)).deleteCommentOfPost(1L, 1L);
    }
}