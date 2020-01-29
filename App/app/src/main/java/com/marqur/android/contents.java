package com.marqur.android;

import java.util.List;

public class contents {
    public String content_id;
    public Boolean contains_media;
    public List<String> media;


    public contents(){

    }


    public contents(String content_id,Boolean contains_media,List media){
        this.contains_media=contains_media;
        this.content_id=content_id;
        this.media=media;
    }
}
