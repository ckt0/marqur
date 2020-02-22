package com.marqur.android;

import java.util.List;

public class Marker {


    public String title;
    public String author;
    public String date_created;
    public String date_modified;
    public List<Double> location;
    public int views;
    public int upvotes;
    public int downvotes;
    public int comments_count;
    public int reports;
    public Content contents;


    public Marker() {

    }


    public Marker(String title, String author, String date_created, String date_modified, List<Double> location, int views, int upvotes, int downvotes, int comments_count, int reports, Content contents) {

        this.title = title;
        this.author = author;
        this.date_created = date_created;
        this.date_modified = date_modified;
        this.location = location;
        this.views = views;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.comments_count = comments_count;
        this.reports = reports;
        this.contents = contents;
    }
}
