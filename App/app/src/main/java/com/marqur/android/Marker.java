package com.marqur.android;

import com.google.firebase.firestore.GeoPoint;


public class Marker {
    public String markerid;
    public String title;
    public String author;
    public String date_created;
    public String date_modified;
    public GeoPoint location;
    public String geohash;
    public int views;
    public int upvotes;
    public int downvotes;
    public int comments_count;
    public int reports;
    public  Content mContent;



    public Marker() {

    }


    public Marker(String markerid,String title, String author,GeoPoint location,String geohash, String date_created, String date_modified, int views, int upvotes, int downvotes, int comments_count, int reports,Content content) {
        this.markerid=markerid;
        this.title = title;
        this.author = author;
        this.date_created = date_created;
        this.date_modified = date_modified;
        this.location=location;
        this.geohash=geohash;
        this.views = views;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.comments_count = comments_count;
        this.reports = reports;
        this.mContent=content;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getDate_modified() {
        return date_modified;
    }

    public void setDate_modified(String date_modified) {
        this.date_modified = date_modified;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getGeohash() {
        return geohash;
    }

    public void setGeohash(String geohash) {
        this.geohash = geohash;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    public int getComments_count() {
        return comments_count;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
    }

    public int getReports() {
        return reports;
    }

    public void setReports(int reports) {
        this.reports = reports;
    }

    public Content getmContent() {
        return mContent;
    }

    public void setmContent(Content mContent) {
        this.mContent = mContent;
    }
}
