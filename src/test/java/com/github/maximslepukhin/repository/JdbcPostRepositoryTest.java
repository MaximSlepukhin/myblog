package com.github.maximslepukhin.repository;

import com.github.maximslepukhin.WebConfiguration;
import com.github.maximslepukhin.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebConfiguration.class)
@TestPropertySource(locations = "classpath:test-application.properties")
class JdbcPostRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM posts");
        jdbcTemplate.execute("DELETE FROM comments");

        jdbcTemplate.execute("ALTER TABLE posts ALTER COLUMN id RESTART WITH 1;");
        jdbcTemplate.execute("ALTER TABLE comments ALTER COLUMN id RESTART WITH 1;");

        jdbcTemplate.execute("INSERT INTO posts (title, text, likesCount, tags) " +
                             "VALUES ('First title', 'First text', 1, 'tags1')");
        jdbcTemplate.execute("INSERT INTO posts (title, text, likesCount, tags) " +
                             "VALUES ('Second title', 'Second text', 2, 'tags2')");
        jdbcTemplate.execute("INSERT INTO comments (text, postId) VALUES ('First comment', 1)");
        jdbcTemplate.execute("INSERT INTO comments (text, postId) VALUES ( 'First comment', 2)");

    }

    @Test
    void findAll_shouldReturnAllPosts() {
        List<Post> posts = postRepository.findPosts(10, 1);

        assertNotNull(posts);
        assertEquals(2, posts.size());

        Post post = posts.getFirst();
        assertEquals("First title", post.getTitle());
        assertEquals("First text", post.getText());
    }

    @Test
    void findById_shouldReturnPostAndComments() {
        Post post = postRepository.findById(1L);

        assertEquals("First title", post.getTitle());
        assertEquals("First text", post.getText());
        assertEquals(1L, post.getId());
        assertEquals("First comment", post.getComments().getFirst().getText());
    }

    @Test
    void shouldSavePost() {
        Post post = new Post(0L, "Third title", "Third text", 3, "tags3", null);
        postRepository.savePost(post);

        Post savedPost = postRepository.findPosts(10, 1).stream()
                .filter(createdPost -> createdPost.getId().equals(3L))
                .findFirst()
                .orElse(null);

        assertNotNull(savedPost);
        assertEquals("Third title", savedPost.getTitle());
        assertEquals("Third text", savedPost.getText());
        assertEquals(3L, savedPost.getId());

    }

    @Test
    void should_addLike() {
        postRepository.addLike(1L, true);

        Post post = postRepository.findById(1L);

        assertNotNull(post);
        assertEquals(2, post.getLikesCount());
    }

    @Test
    void shouldUpdatePost() {
        Post post = new Post(1L, "Updated title", "Updated text", 1, "tags1", null);
        postRepository.updatePost(post);

        Post updatedPost = postRepository.findById(post.getId());

        assertNotNull(updatedPost);
        assertEquals("Updated title", updatedPost.getTitle());
        assertEquals("Updated text", updatedPost.getText());
        assertEquals(1L, updatedPost.getId());
        assertEquals("First comment", updatedPost.getComments().getFirst().getText());
    }

    @Test
    void shouldDeletePostById() {
        postRepository.deletePostById(1L);

        List<Post> posts = postRepository.findPosts(10, 1);

        boolean contains = posts.stream()
                .noneMatch(createdPosts -> createdPosts.getId().equals(1L));

        assertTrue(contains);
    }
}