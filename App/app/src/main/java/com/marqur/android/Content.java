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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Media> getMedia() {
        return media;
    }

    public void setMedia(List<Media> media) {
        this.media = media;
    }
}
