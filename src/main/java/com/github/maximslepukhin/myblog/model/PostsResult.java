package com.github.maximslepukhin.model;

import java.util.List;

public class PostsResult {
    private List<Post> posts;
    private PageInfo pageInfo;

    public PostsResult(List<Post> posts, boolean hasNextPage, int page, int pageSize) {
        this.posts = posts;
        boolean hasPreviousPage = page > 1;
        PageInfo info = new PageInfo(page, pageSize, hasNextPage, hasPreviousPage);
        this.pageInfo = info;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }
}

