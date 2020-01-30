package com.marqur.android;

public class Report {
    public String report_id;
    public String subject_id;
    public String author;
    public String date;
    public String type;
    public String text;

    public Report() {
    }

    public Report(String report_id, String subject_id, String author, String date, String type, String text) {
        this.report_id = report_id;
        this.subject_id = subject_id;
        this.author = author;
        this.date = date;
        this.type = type;
        this.text = text;
    }
}
