package com.github.maximslepukhin.model;


import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    private Long id;
    private String title;
    private String text;
    private int likesCount;
    private String tags;
    private List<Comment> comments;
}
