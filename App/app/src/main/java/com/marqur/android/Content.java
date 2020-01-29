package com.marqur.android;

import java.util.List;

public class Content {
    public String content_id;
    public Boolean contains_media;
    public List<Media> media;


    public Content(){

    }


    public Content(String content_id, Boolean contains_media, List<Media> media){
        this.contains_media=contains_media;
        this.content_id=content_id;
        this.media=media;
    }
}
