package de.nedeco.aufgabe.aufgabenedeco;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 09.12.2017.
 */

public class xmlParsing extends AsyncTask<String, Void, List> {

    InputStream inputStream;
    String title = null;
    String destcription = null;
    String link = null;
    String date = null;
    String content = null;
    String pic =null;
    List entries;


    private static final String ns = null;
    protected List doInBackground(String... urls) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        try {
            URL url = new URL(urls[0]);
            inputStream = url.openConnection().getInputStream();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            parser.nextTag();
            return readFeed(parser);




        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            return null;
        }
        return new ArrayList();
    }
    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        entries = new ArrayList();
        parser.require(XmlPullParser.START_TAG, ns, "rss");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("channel")) {
                readEntry(parser);
            } else {
                skip(parser);
            }
        }
        return entries;
    }


    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "channel");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("item")) {
                entries.add(readItem(parser));
            } else {
                skip(parser);
            }
        }
        return new Entry(title, destcription, link, date, content, pic);
    }
    private Entry readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "item");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                title = readContent(parser,"title");
            } else if (name.equals("link")) {
                link = readContent(parser,"link");
            } else if (name.equals("description")) {
                destcription = readContent(parser,"description");
            }else if (name.equals("pubDate")){
                date = readContent(parser,"pubDate");
            }else if (name.equals("content:encoded")){
                content = readContent(parser,"content:encoded");
            }else if (name.equals("enclosure")){
                pic = readPic(parser,"enclosure");
            } else {
                skip(parser);
            }

        }
        return new Entry(title, destcription, link, date, content, pic);
    }
    // Processes tags in Item.
    private String readContent(XmlPullParser parser,String category) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, category);
        String text = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, category);
        return text;
    }

    private String readPic(XmlPullParser parser,String category) throws IOException, XmlPullParserException {
        String pic = "";
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


        // extract text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

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

