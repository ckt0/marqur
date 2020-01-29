package com.marqur.android;

import java.util.List;

public class markers {

    public String Marker_id;
    public String Title;
    public String author;
    public String date_crated;
    public String date_modified;
    public Double Lattitude;
    public Double Longitude;
    public Long Views;
    public Long upvotes;
    public Long downvotes;
    public Long comments;
    public String thread_id;
    public Long reports;
    public List<String> contents;


    public markers(){

    }


    public markers(String Marker_id,String Title,String author,String date_crated,String date_modified,Double Lattitude,Double Longitude,Long Views,Long upvotes,Long downvotes,Long comments,String thread_id,Long reports,List<String> contents)
    {
        this.Marker_id=Marker_id;
        this.Title=Title;
        this.author=author;
        this.date_crated=date_crated;
        this.date_modified=date_modified;
        this.Lattitude=Lattitude;
        this.Longitude=Longitude;
        this.Views=Views;
        this.upvotes=upvotes;
        this.downvotes=downvotes;
        this.comments=comments;
        this.thread_id=thread_id;
        this.reports=reports;
        this.contents=contents;
    }
}
