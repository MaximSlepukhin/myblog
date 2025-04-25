package com.github.maximslepukhin.repository;

import com.github.maximslepukhin.model.Comment;
import com.github.maximslepukhin.model.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Repository
public class JdbcPostRepository implements PostRepository {


    private final JdbcTemplate jdbcTemplate;

    public JdbcPostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Post> findPosts(int pageSize, int page) {
        int offset = (page - 1) * pageSize;

        pageSize++;

        String sql =
                """
                        select id, title, text, likesCount, tags
                        from posts
                        limit ?
                        offset ?
                        """;
        List<Post> posts = jdbcTemplate.query(
                sql,
                new Object[]{pageSize, offset},
                (rs, rowNum) -> new Post(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("text"),
                        rs.getInt("likesCount"),
                        rs.getString("tags"),
                        new ArrayList<>()
                )
        );

        for (Post post : posts) {
            List<Comment> comments = jdbcTemplate.query(
                    "select id, text, postId from comments where postId = ?",
                    new Object[]{post.getId()},
                    (rs, rowNum) -> new Comment(
                            rs.getLong("id"),
                            rs.getString("text"),
                            rs.getLong("postId")
                    ));
            post.setComments(comments);
        }
        return posts;
    }

    @Override
    public Post findById(Long postId) {

        String sql = "SELECT id, title, text, likesCount, tags FROM posts WHERE id = ?";

        Post post = jdbcTemplate.queryForObject(sql, new Object[]{postId}, (rs, rowNum) -> {
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

    @Override
    public Post savePost(Post post) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into posts(title, text, likesCount, tags) values(?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getText());
            ps.setInt(3, post.getLikesCount());
            ps.setString(4, post.getTags());
            return ps;
        }, keyHolder);

        post.setId(keyHolder.getKey().longValue());
        return post;
    }

    @Override
    public void addLike(Long postId, boolean like) {
        Post post = findById(postId);
        if (post == null) {
            throw new IllegalArgumentException("Post not found");
        }
        if (like) {
            post.setLikesCount(post.getLikesCount() + 1);
        } else {
            post.setLikesCount(post.getLikesCount() - 1);
        }
        String sql = "UPDATE posts SET likesCount = ? WHERE id = ?";
        jdbcTemplate.update(sql, post.getLikesCount(), postId);
    }

    @Override
    public void updatePost(Post post) {
        String sql = "UPDATE POSTS SET title = ?, text = ?, likesCount = ?, tags = ? where id = ?";
        jdbcTemplate.update(sql, post.getTitle(), post.getText(), post.getLikesCount(), post.getTags(), post.getId());
    }

    @Override
    public void deletePostById(Long postId) {
        String sql = "DELETE FROM POSTS WHERE ID = ?";
        jdbcTemplate.update(sql, postId);
    }
}
