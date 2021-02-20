package com.arulvakku.ui.model;

public class BookModel {
    String chapter_name;
    String chapter_id;
    String chapter_count;
    String chapter_type;
    String chapter_share_no;

    public String getChapter_share_no() {
        return chapter_share_no;
    }

    public void setChapter_share_no(String chapter_share_no) {
        this.chapter_share_no = chapter_share_no;
    }

    public BookModel() {

    }

    public String getChapter_name() {
        return chapter_name;
    }

    public void setChapter_name(String chapter_name) {
        this.chapter_name = chapter_name;
    }

    public String getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(String chapter_id) {
        this.chapter_id = chapter_id;
    }

    public String getChapter_count() {
        return chapter_count;
    }

    public void setChapter_count(String chapter_count) {
        this.chapter_count = chapter_count;
    }

    public String getChapter_type() {
        return chapter_type;
    }

    public void setChapter_type(String chapter_type) {
        this.chapter_type = chapter_type;
    }
}
