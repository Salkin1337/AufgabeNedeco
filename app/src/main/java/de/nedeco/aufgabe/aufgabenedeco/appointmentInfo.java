package de.nedeco.aufgabe.aufgabenedeco;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by User on 10.12.2017.
 */

public class appointmentInfo extends AppCompatActivity {

    String link;
    String date;
    String content;
    String pic;
    String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_info);

        WebView contentView = (WebView) findViewById(R.id.content);
        TextView linkView = (TextView) findViewById(R.id.link);
        TextView dateView = (TextView) findViewById(R.id.date);
        ImageView picView = (ImageView) findViewById(R.id.pictureWebView);


        link = getIntent().getStringExtra("link");
        date = getIntent().getStringExtra("date");
        content = getIntent().getStringExtra("content");
        pic = getIntent().getStringExtra("pic");


        try {
            pic = "https" + pic.split("http")[1];
        }catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }

        dateView.setText(date);
        contentView.getSettings().setJavaScriptEnabled(true);
        contentView.loadDataWithBaseURL(null,"<html>" + content+"</html>", "text/html", "utf-8", null);;
        linkView.setText(link);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        try {
            InputStream is  = (InputStream) new URL(pic).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            picView.setImageDrawable(d);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
