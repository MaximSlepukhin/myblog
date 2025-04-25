package com.github.maximslepukhin.controller;

import com.github.maximslepukhin.WebConfiguration;
import com.github.maximslepukhin.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebConfiguration.class)
@TestPropertySource(locations = "classpath:test-application.properties")
class HomeControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        jdbcTemplate.execute("DELETE FROM posts");
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("ALTER TABLE posts ALTER COLUMN id RESTART WITH 1;");
        jdbcTemplate.execute("INSERT INTO posts (id, title, text, likesCount, tags)" +
                             "VALUES (1, 'First title', 'First text', 1, 'tags1')");
        jdbcTemplate.execute("INSERT INTO posts (id, title, text, likesCount, tags)" +
                             "VALUES (2, 'Second title', 'Second text', 2, 'tags2')");

    }

    @Test
    void should_redirectToPosts() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));
    }

    @Test
    void should_getPosts() throws Exception {
        mockMvc.perform(get("/posts")
                        .param("search", "First title")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("search"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(model().attributeExists("posts"));
    }

    @Test
    void should_getPostById() throws Exception {
        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("post"))
                .andExpect(view().name("post"));
    }

    @Test
    void should_showPageWithAddingPost() throws Exception {
        mockMvc.perform(get("/posts/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-post"));
    }

    @Test
    void should_addPost() throws Exception {
        jdbcTemplate.execute("DELETE FROM posts");

        Post post = new Post(
                3L,
                "Title",
                "Text",
                0,
                "Tags",
                null
        );

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", post.getTitle())
                        .param("tags", post.getTags())
                        .param("text", post.getText()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));


        Integer actual = jdbcTemplate.queryForObject("select count(*) from posts", Integer.class);

        assertNotNull(actual);
        assertEquals(1, actual);
    }

    @Test
    void should_showImage() {

    }

    @Test
    void should_addLike() throws Exception {
        mockMvc.perform(post("/posts/1/like")
                        .param("like", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));
        Integer totalLikes = jdbcTemplate.queryForObject("SELECT likesCount FROM posts WHERE id = ?", new Object[]{1}, Integer.class);
        assertEquals(2, totalLikes);
    }

    @Test
    void should_showEditPostForm() throws Exception {
        mockMvc.perform(get("/posts/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-post"));
    }

    @Test
    void should_updatePost() throws Exception {
        Long postId = 1L;
        Post updatedPost = new Post(
                postId,
                "Update title",
                "Update text",
                0,
                "Tags",
                null);

        MockMultipartFile imageFile = new MockMultipartFile("image", "image.jpg", "image/jpeg", "image content".getBytes());

        mockMvc.perform(multipart("/posts/{id}", postId)
                        .file(imageFile) // добавляем файл изображения
                        .param("title", updatedPost.getTitle())
                        .param("tags", updatedPost.getTags())
                        .param("text", updatedPost.getText()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + postId));
    }

    @Test
    void should_addComment() throws Exception {
        mockMvc.perform(post("/posts/1/comments")
                        .param("text", "text"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlTemplate("/posts/1"));
    }

    @Test
    void should_editComment() throws Exception {
        mockMvc.perform(post("/posts/1/comments/1")
                        .param("text", "text"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlTemplate("/posts/1"));
    }


    @Test
    void should_deleteComment() throws Exception {
        mockMvc.perform(post("/posts/1/comments/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlTemplate("/posts/1"));
    }

    @Test
    void should_deletePostById() throws Exception {
        mockMvc.perform(post("/posts/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlTemplate("/posts"));
    }
}