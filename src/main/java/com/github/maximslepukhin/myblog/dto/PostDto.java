package com.github.maximslepukhin.dto;

import com.github.maximslepukhin.model.Comment;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Long id;
    private String title;
    private String textPreview;
    private int likesCount;
    private List<String> tags;
    private List<Comment> comments;
    private String tagsAsText;
    private String text;
    private List<String> textParts;
}