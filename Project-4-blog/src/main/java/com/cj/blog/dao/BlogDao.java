package com.cj.blog.dao;

import com.cj.blog.model.BlogEntry;

import java.util.List;

public interface BlogDao {
    boolean addEntry(BlogEntry blogEntry);
    List<BlogEntry> findAllEntries();
    BlogEntry findEntryBySlug(String slug);
    boolean removeBlogEntry(BlogEntry blogEntry);
    boolean editBlogEntry(String id, String title, String text);
}
