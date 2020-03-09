package com.marqur.android;

import com.google.firebase.firestore.GeoPoint;


public class Marker {

    public String title;
    public String author;
    public String date_created;
    public String date_modified;
    public GeoPoint location;
    public int views;
    public int upvotes;
    public int downvotes;
    public int comments_count;
    public int reports;



    public Marker() {

    }


    public Marker(String title, String author,GeoPoint location, String date_created, String date_modified, int views, int upvotes, int downvotes, int comments_count, int reports) {

        this.title = title;
        this.author = author;
        this.date_created = date_created;
        this.date_modified = date_modified;
        this.location=location;
        this.views = views;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.comments_count = comments_count;
        this.reports = reports;

    }


}
