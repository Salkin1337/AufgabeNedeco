package de.nedeco.aufgabe.aufgabenedeco;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 09.12.2017.
 */

public class XmlParsing extends AsyncTask<String, Void, List> {

    InputStream inputStream;
    String title;
    String description;
    String link;
    String date;
    String content;
    String pic;
    String category;
    List entries;

    private static final String ns = null;

    // Parses the given Url to entries List
    protected List doInBackground(String... xmlUrl) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        entries = new ArrayList();


        try {
            URL url = new URL(xmlUrl[0]);
            inputStream = url.openConnection().getInputStream();
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            parser.nextTag();
            return readFeed(parser);
        } catch (Exception e) {
            e.printStackTrace();
            //If the given Link for some Reason is not an RSSFeed, XmlPullParserException will be added to Error in entries
            //If the inputStream for some reason can't connect to the given url, IOException will be added to Error in entries.
            //If the Url is not valid for some reason, MalformedURLException will be added to Error in entries.
            entries.add(new Entry("","","","","","","",e.getClass().getName()));
            try {
                inputStream.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return entries;
    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "rss");
        parser.nextTag();

        String name = parser.getName();
        // Starts by looking for the channel and than for the item tag
        try {
            if (name.equals("channel")) {
                parser.require(XmlPullParser.START_TAG, ns, "channel");
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    name = parser.getName();
                    if (name.equals("item")) {
                        entries.add(readItem(parser));
                    } else {
                        skip(parser);
                    }
                }
            } else {
                skip(parser);
            }
        }catch (NullPointerException e) {
            e.printStackTrace();
            inputStream.close();
            //If the content of the given site is for some reason .
            // error in entries is set to NullPointerException.
            // So it is in MainActivity clear what happend her.
            entries.add(new Entry("","","","","","","",e.getCause().toString()));
        }
        inputStream.close();
        return entries;
    }

    // Parses the contents of an item in channel. If it encounters a title,link,.etc tag, hands them off
    // to their respective methods for processing. Otherwise, skips the tag.
    private Entry readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "item");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch(name) {
                case "title":
                    title = readContent(parser,"title");
                    break;
                case "link":
                    link = readContent(parser,"link");
                    break;
                case "description":
                    description = readContent(parser,"description");
                    break;
                case "pubDate":
                    date = readContent(parser,"pubDate");
                    break;
                case "content:encoded":
                    content = readContent(parser,"content:encoded");
                    break;
                case "enclosure":
                    pic = readPic(parser, "enclosure");
                    break;
                case "category":
                    category = readContent(parser,"category");
                    break;
                default:
                    skip(parser);
            }
        }
        return new Entry(title, description, link, date, content, pic, category,"");
    }

    // Processes tags in Item.
    private String readContent(XmlPullParser parser,String category) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, category);
        String text = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, category);
        return text;
    }

    // Gets the Link to the Picture in the XMLParser
    private String readPic(XmlPullParser parser,String category) throws IOException, XmlPullParserException {
        String pic = null;
        parser.require(XmlPullParser.START_TAG, ns, category);
        String tag = parser.getName();
        String lenType = parser.getAttributeValue(null, "length");
        String imType = parser.getAttributeValue(null, "type");
        if (tag.equals(category)) {
            if (lenType.equals("500")&&imType.equals("image/jpeg")) {
                pic = parser.getAttributeValue(null, "url");
                parser.nextTag();
            }
        }
        return pic;
    }


    //Extract text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    //Skips the current Tag
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
