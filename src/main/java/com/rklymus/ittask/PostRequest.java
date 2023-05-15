package com.rklymus.ittask;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostRequest {
    @NotNull
    private String title;
    @NotNull
    private String content;

    public Post toEntity() {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        return post;
    }
}
