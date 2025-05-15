package com.github.maximslepukhin.service;

import com.github.maximslepukhin.myblog.model.Post;
import com.github.maximslepukhin.myblog.repository.CommentRepository;
import com.github.maximslepukhin.myblog.service.CommentServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = CommentServiceImpl.class)
class CommentServiceImplTest {

    @MockBean
    private CommentRepository commentRepository;

    @Autowired
    private CommentServiceImpl commentService;

    @Test
    void shouldAddComment() {
        commentService.addComment(1L, "Comment");
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