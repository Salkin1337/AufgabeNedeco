package de.nedeco.aufgabe.aufgabenedeco;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 09.12.2017.
 */

public class XmlParsing extends AsyncTask<String, Void, List> {

    InputStream inputStream;
    String[] titleDate;
    String description;
    String link;
    String pubDate;
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

            // Pre Parses URL to replace invalid Characters like "&"
            byte[] buffer = new byte[1024];
            StringBuilder rssString = new StringBuilder();
            while(inputStream.read(buffer) > 0) {
                rssString.append(new String(buffer, StandardCharsets.UTF_8));
            }

            inputStream = new ByteArrayInputStream(rssString.toString().replaceAll(" & ", " &amp; ").getBytes());

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
            entries.add(new Entry("","" ,"","","","","","",e.getClass().getName()));
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
            entries.add(new Entry("","" ,"","","","","","",e.getCause().toString()));
        }
        inputStream.close();
        return entries;
    }

    // Parses the contents of an item in channel. If it encounters a title,link,.etc tag, hands them off
    // to their respective methods for processing. Otherwise, skips the tag.
    private Entry readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "item");
        try {
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                switch (name) {
                    case "title":
                        titleDate = readContent(parser, "title").split("Uhr:");
                        break;
                    case "link":
                        link = readContent(parser, "link");
                        break;
                    case "description":
                        description = readContent(parser, "description");
                        break;
                    case "pubDate":
                        pubDate = readContent(parser, "pubDate");
                        break;
                    case "content:encoded":
                        content = readContent(parser, "content:encoded");
                        break;
                    case "enclosure":
                        pic = readPic(parser, "enclosure");
                        break;
                    case "category":
                        category = readContent(parser, "category");
                        break;
                    default:
                        skip(parser);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new Entry(titleDate[1],titleDate[0]+"Uhr", description, link, pubDate, content, pic, category,"");
    }

    // Processes tags in Item.
    private String readContent(XmlPullParser parser,String category) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, category);
        String text = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, category);
        return text;
    }

    // Gets the Link to the Picture in the XMLParser
    private String readPic(XmlPullParser parser,String category) throws IOException,XmlPullParserException {
        String pic = null;
        parser.require(XmlPullParser.START_TAG, ns, category);
        String tag = parser.getName();
        String imType = parser.getAttributeValue(null, "type");
        if (tag.equals(category)) {
            if (imType.equals("image/jpeg")) {
                pic = parser.getAttributeValue(null, "url");
                parser.nextTag();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, category);
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
