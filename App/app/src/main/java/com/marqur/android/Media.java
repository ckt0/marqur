package com.marqur.android;

public class Media {
    public String media_id;
    public String caption;

    Media() {

    }

    Media(String media_id, String caption) {
        this.media_id = media_id;
        this.caption = caption;
    }

    public String getMedia_id() {
        return media_id;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
