package com.marqur.android;

import java.util.List;

public class Marker {

    public String marker_id;
    public String title;
    public String author;
    public String date_created;
    public String date_modified;
    public List<Double> location;
    public Long views;
    public Long upvotes;
    public Long downvotes;
    public Long comments;
    public String thread_id;
    public Long reports;
    public List<Content> contents;


    public Marker(){

    }


    public Marker(String marker_id, String title, String author, String date_created, String date_modified, List<Double> location, Long views, Long upvotes, Long downvotes, Long comments, String thread_id, Long reports, List<Content> contents)
    {
        this.marker_id=marker_id;
        this.title=title;
        this.author=author;
        this.date_created=date_created;
        this.date_modified=date_modified;
        this.location=location;
        this.views=views;
        this.upvotes=upvotes;
        this.downvotes=downvotes;
        this.comments=comments;
        this.thread_id=thread_id;
        this.reports=reports;
        this.contents = contents;
    }
}
