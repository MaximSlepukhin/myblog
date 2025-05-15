package com.github.maximslepukhin.service;

import com.github.maximslepukhin.myblog.model.Post;
import com.github.maximslepukhin.myblog.model.PostsResult;
import com.github.maximslepukhin.myblog.repository.PostRepository;
import com.github.maximslepukhin.myblog.service.PostServiceImpl;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(PostServiceImpl.class)
public class ServiceTest {

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private HttpServletRequest request;

    @Autowired
    private PostServiceImpl postService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void shouldSavePost() {
        Post postToSave = new Post(1L, "New Title", "New text", 0, "tags", null);

        when(postRepository.savePost(any(Post.class)))
                .thenReturn(postToSave);

        Post savedPost = postService.savePost(postToSave);

        assertNotNull(savedPost);
        assertEquals("New Title", savedPost.getTitle());
        assertEquals("New text", savedPost.getText());
        assertEquals(0, savedPost.getLikesCount());
        verify(postRepository, times(1)).savePost(postToSave);
    }

    @Test
    void shouldFindPostById() {
        Post existingPost = new Post(1L, "Existing Title", "Existing Text", 0,
                "tags", null);

        when(postRepository.findById(1L)).thenReturn(existingPost);

        Post foundPost = postService.findPostById(1L);

        assertNotNull(foundPost);
        assertEquals("Existing Title", foundPost.getTitle());
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void shouldFindPosts() {
        List<Post> postList = new ArrayList<>();
        postList.add(new Post(1L, "Title 1", "Text 1", 0, "tags", null));
        postList.add(new Post(2L, "Title 2", "Text 2", 0, "tags", null));

        when(postRepository.findPosts(2, 0)).thenReturn(postList);

        PostsResult result = postService.findPosts(2, 0);

        assertNotNull(result);
        assertEquals(2, result.getPosts().size());
        verify(postRepository, times(1)).findPosts(2, 0);
    }

    @Test
    void shouldDeletePostById() throws IOException {
        Long postIdToDelete = 1L;
        String path = "some/path/to/images/";
        ServletContext servletContext = mock(ServletContext.class);

        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getRealPath("/")).thenReturn(path);
        doNothing().when(postRepository).deletePostById(postIdToDelete);

        postService.deletePostById(postIdToDelete, request);

        verify(postRepository, times(1)).deletePostById(postIdToDelete);
    }
}
