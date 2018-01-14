package de.nedeco.aufgabe.aufgabenedeco;


/**
 * Created by User on 09.12.2017.
 * Date class with getter
 */

public class Entry {

    public final String title;
    public final String date;
    public final String link;
    public final String description;
    public final String pubDate;
    public final String content;
    public final String pic;
    public final String category;
    public final String error;

    Entry(String title,String date, String description, String link, String pubDate, String content, String pic, String category,String error) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.link = link;
        this.pubDate = pubDate;
        this.content = content;
        this.pic = pic;
        this.category = category;
        this.error = error;

    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getContent() {
        return content;
    }

    public String getPic() {
        return pic;
    }

    public  String getCategory() {
        return category;
    }

    public String getError() {
        return error;
    }
}
