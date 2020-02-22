package com.marqur.android;

import java.util.List;

public class Content {

    public String title;
    public String text;

    public List<Media> media;


    public Content() {

    }


    public Content(String Title, String text, List<Media> media) {
        this.text = text;
        this.title = Title;

        this.media = media;
    }


}
