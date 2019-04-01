package com.leafyjava.tutorials.tccdemo.services;

public interface TccCompliantService<T, R> {
    R trying(T resource);
    void cancel(String id);
    void confirm(String id);
}
