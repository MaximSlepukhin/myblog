package com.github.maximslepukhin.repository;

import com.github.maximslepukhin.myblog.model.Comment;
import com.github.maximslepukhin.myblog.model.Post;
import com.github.maximslepukhin.myblog.repository.CommentRepository;
import com.github.maximslepukhin.myblog.repository.JdbcCommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import(JdbcCommentRepository.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:test-application.properties")
public class JdbcCommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
        jdbcTemplate.execute("INSERT INTO comments (text, postId) VALUES ('Comment for first post', 1)");
        jdbcTemplate.execute("INSERT INTO comments (text, postId) VALUES ( 'Comment for second post', 2)");
    }


    @Test
    void shouldAddComment() {
        String text = "Second comment for first post";
        commentRepository.addComment(1L, text);
        String sql = "SELECT id, text, postId FROM comments WHERE id = ?";
        Comment comment = getComment(3L, sql);

        assertNotNull(comment);
        assertEquals(text, comment.getText());
    }

    @Test
    void shouldEditCommentOfPost() {
        String sqlForPost = "SELECT id, title, text, likesCount, tags FROM posts WHERE id = ?";
        Post post = getPost(1L, sqlForPost);
        commentRepository.editCommentOfPost(post, 1L, "New comment");
        String sqlForComment = "SELECT id, text, postId FROM comments WHERE id = ?";
        Comment comment = getComment(1L, sqlForComment);

        assertNotNull(comment);
        assertEquals("New comment", comment.getText());
    }

    @Test
    void shouldGetCommentsOfPost() {
        String sql = "SELECT id, title, text, likesCount, tags FROM posts WHERE id = ?";
        Post post = getPost(1L, sql);
        List<Comment> listOfComments = post.getComments();

        assertNotNull(listOfComments);
        assertEquals(1, listOfComments.size());
    }

    @Test
    void shouldDeleteCommentOfPost() {
        commentRepository.deleteCommentOfPost(1L, 1L);
        String sql = "SELECT id, title, text, likesCount, tags FROM posts WHERE id = ?";
        Post post = getPost(1L, sql);
        List<Comment> listOfComments = post.getComments();

        assertTrue(listOfComments.isEmpty());
    }

    private Comment getComment(Long id, String sql) {
        Comment comment = jdbcTemplate.query(
                sql,
                new Object[]{id},
                (rs, rowNum) -> new Comment(
                        rs.getLong("id"),
                        rs.getString("text"),
                        rs.getLong("postId")
                )
        ).stream().findFirst().orElse(null);
        return comment;
    }

    private Post getPost(Long id, String sql) {
        Post post = jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> {
            Post p = new Post();
            p.setId(rs.getLong("id"));
            p.setTitle(rs.getString("title"));
            p.setText(rs.getString("text"));
            p.setLikesCount(rs.getInt("likesCount"));
            p.setTags(rs.getString("tags"));
            return p;
        });
        List<Comment> comments = getCommentsOfPost(post);
        post.setComments(comments);
        return post;
    }

    private List<Comment> getCommentsOfPost(Post post) {
        return jdbcTemplate.query(
                "SELECT id, text, postId FROM comments WHERE postId = ?",
                new Object[]{post.getId()},
                (rs, rowNum) -> new Comment(
                        rs.getLong("id"),
                        rs.getString("text"),
                        rs.getLong("postId")
                )
        );
    }
}