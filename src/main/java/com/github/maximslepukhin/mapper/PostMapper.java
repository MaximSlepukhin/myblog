package com.github.maximslepukhin.mapper;

import com.github.maximslepukhin.dto.PostDto;
import com.github.maximslepukhin.model.Post;

import java.util.Arrays;
import java.util.List;

public class PostMapper {
    public static PostDto toPostDto(Post post) {
        List<String> tagList = Arrays.asList(post.getTags().split(" "));
        List<String> textParts = Arrays.asList(post.getText().split("\n"));
        String tagsAsText = String.join(" ", tagList);
        int commentCount = post.getComments().size();

        return new PostDto(
                post.getId(),
                post.getTitle(),
                post.getText(),
                post.getLikesCount(),
                tagList,
                post.getComments(),
                tagsAsText,
                post.getText(),
                textParts
        );
    }
}
