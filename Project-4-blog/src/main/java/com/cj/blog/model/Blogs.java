package com.cj.blog.model;


import com.cj.blog.dao.BlogDao;

import java.util.ArrayList;
import java.util.List;


public class Blogs implements BlogDao {

    private List<BlogEntry> blogEntries = new ArrayList<>();


    public Blogs() {}

    @Override
    public boolean addEntry(BlogEntry blogEntry) {
        return blogEntries.add(blogEntry);
    }

    @Override
    public List<BlogEntry> findAllEntries() {
        return blogEntries;
    }

    @Override
    public BlogEntry findEntryBySlug(String slug) {
        return blogEntries.stream()
                .filter(entry -> entry.getSlug().equals(slug))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public boolean removeBlogEntry(BlogEntry blogEntry) {
        return blogEntries.remove(blogEntry);
    }

    @Override
    public boolean editBlogEntry(String slug, String title, String text) {
        try {
            BlogEntry blogEntry = findEntryBySlug(slug);
            blogEntry.setTitle(title + " (edited)");
            blogEntry.setText(text);
            return true;
        }
        catch (Exception exc) {
            return false;
        }
    }
}
