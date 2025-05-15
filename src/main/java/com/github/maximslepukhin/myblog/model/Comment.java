package com.github.maximslepukhin.myblog.model;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private Long id;
    private String text;
    private Long postId;
}
