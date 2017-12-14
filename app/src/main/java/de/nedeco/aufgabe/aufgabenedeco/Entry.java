package de.nedeco.aufgabe.aufgabenedeco;

/**
 * Created by User on 09.12.2017.
 */

public class Entry {
    public final String title;
    public final String link;
    public final String discription;
    public final String date;
    public final String content;
    public final String pic;

    Entry(String title, String discription, String link, String date, String content, String pic) {
        this.title = title;
        this.discription = discription;
        this.link = link;
        this.date = date;
        this.content = content;
        this.pic = pic;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDiscription() {
        return discription;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public String getPic() {
        return pic;
    }
}
