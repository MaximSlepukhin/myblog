package com.github.maximslepukhin.repository;

import com.github.maximslepukhin.model.Comment;
import com.github.maximslepukhin.model.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcCommentRepository implements CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcCommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void  addComment(Long postId, String text) {
        String sql = "INSERT INTO comments (text, postId) VALUES (?, ?)";
        jdbcTemplate.update(sql, text, postId);

    }

    @Override
    public void editCommentOfPost(Post post, Long commentId, String text) {
        List<Comment> comments = getCommentsOfPost(post);
        Comment comment = comments.stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst()
                .orElse(null);
        if (comment == null) {
            return;
        }
        comment.setText(text);
        String sql = "UPDATE comments SET text = ? WHERE postId = ? AND id = ?";
        jdbcTemplate.update(sql, text, post.getId(), commentId);
    }

    @Override
    public List<Comment> getCommentsOfPost(Post post) {
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

    @Override
    public void deleteCommentOfPost(Long postId, Long commentId) {
        String sql = "DELETE FROM comments WHERE postId = ? AND id = ?";
        jdbcTemplate.update(sql, postId, commentId);
    }
}
