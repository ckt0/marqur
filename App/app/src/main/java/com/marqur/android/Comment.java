package com.marqur.android;

import java.util.List;

public class Comment {
    public String marker_id;
    public String parent_id;
    public String comment_id;
    public String content;
    public List<String> replies;
    public Double upvotes;
    public Double downvotes;
    public Double reply_count;

    Comment() {

    }

    Comment(String marker_id, String parent_id, String comment_id, String content, List<String> replies, Double upvotes, Double downvotes, Double reply_count) {

        this.marker_id = marker_id;
        this.parent_id = parent_id;
        this.comment_id = comment_id;
        this.content = content;
        this.replies = replies;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.reply_count = reply_count;
    }
}
