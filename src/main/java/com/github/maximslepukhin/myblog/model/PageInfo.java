package com.github.maximslepukhin.model;

public class PageInfo {
    private int pageNumber;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;

    public PageInfo(int pageNumber, int pageSize, boolean hasNext, boolean hasPrevious) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }

    public int pageNumber() {
        return pageNumber;
    }

    public int pageSize() {
        return pageSize;
    }


    public boolean hasNext() {
        return hasNext;
    }


    public boolean hasPrevious() {
        return hasPrevious;
    }

}
