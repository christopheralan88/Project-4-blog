package com.cj.blog.model;

import com.github.slugify.Slugify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class BlogEntry {

    private List<Comment> comments = new ArrayList<>();
    private String title;
    private String user;
    private String text;
    private Date dateTime;
    private List<String> tags = new ArrayList<>();
    private String slug;


    public BlogEntry(String title, String user, String text, String[] tags) {
        this.title = title;
        this.user = user;
        this.text = text;
        if (tags != null) {
            this.tags.addAll(Arrays.asList(tags));
        }
        this.dateTime = new Date();

        Slugify slugify = new Slugify();
        this.slug = slugify.slugify(title);
    }

    public List<String> getTags() {
        return tags;
    }

    public String getText() {
        return text;
    }

    public String getUser() {
        return user;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public String getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    //TODO:  instead of try-catch block, throw exception and have try-catch block in calling method?
    public boolean addComment(BlogEntry blogEntry, String user, String text) {
        try {
            Comment comment = new Comment(blogEntry, user, text);
            comments.add(comment);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    //TODO:  instead of try-catch block, throw exception and have try-catch block in calling method?
    public boolean removeComment(Comment comment) {
        try {
            comments.remove(comment);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public boolean addTag(String tag) {
        return tags.add(tag);
    }

    public boolean removeTag(String tag) {
        return tags.remove(tag);
    }

    public int compare(BlogEntry b1, BlogEntry b2) {
        return b1.getTitle().compareTo(b2.getTitle());
    }
}
