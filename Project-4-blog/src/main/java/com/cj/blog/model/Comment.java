package com.cj.blog.model;

import java.util.Date;


public class Comment {

    private BlogEntry blogEntry;
    private String user;
    private Date dateTime;
    private String text;

    public Comment(BlogEntry blogEntry, String user, String text) {
        this.blogEntry = blogEntry;
        this.user = user;
        this.dateTime = new Date(); // instantiated Date object contains current time that object was created
        this.text = text;
    }

    public BlogEntry getBlogEntry() {
        return blogEntry;
    }

    public String getUser() {
        return user;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "blogEntry=" + blogEntry +
                ", user='" + user + '\'' +
                ", dateTime=" + dateTime +
                ", text='" + text + '\'' +
                '}';
    }
}
