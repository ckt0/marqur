package com.marqur.android;

import java.util.List;

public class Content {
    public String content_id;
    public String text;

    public List<Media> media;


    public Content(){

    }


    public Content(String content_id,String text, List<Media> media){
        this.text=text;
        this.content_id=content_id;
        this.media=media;
    }

    public Content(String content_id,String text){
        this.text=text;
        this.content_id=content_id;
    }
}
