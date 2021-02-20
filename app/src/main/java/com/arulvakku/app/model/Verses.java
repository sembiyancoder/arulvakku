package com.arulvakku.app.model;

public class Verses {

    String verse_id;
    String book_id;
    String chapter_id;
    String full_id;
    String verse;
    String verse_type;

    public String getVerse_id() {
        return verse_id;
    }

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public void setVerse_id(String verse_id) {
        this.verse_id = verse_id;
    }



    public String getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(String chapter_id) {
        this.chapter_id = chapter_id;
    }

    public String getFull_id() {
        return full_id;
    }

    public void setFull_id(String full_id) {
        this.full_id = full_id;
    }

    public String getVerse() {
        return verse;
    }

    public void setVerse(String verse) {
        this.verse = verse;
    }

    public String getVerse_type() {
        return verse_type;
    }

    public void setVerse_type(String verse_type) {
        this.verse_type = verse_type;
    }
}
