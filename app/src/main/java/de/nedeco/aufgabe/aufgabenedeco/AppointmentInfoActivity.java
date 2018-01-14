package de.nedeco.aufgabe.aufgabenedeco;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by User on 10.12.2017.
 */

//List title,date,published date,link,description an pictures.
public class AppointmentInfoActivity extends AppCompatActivity {

    String title;
    String date;
    String link;
    String pubDate;
    String content;
    String pic;
    Drawable picDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.appointment_info);

        overridePendingTransition(R.anim.animationin_rl, R.anim.animationout_rl);


        TextView titleView = (TextView)findViewById(R.id.title) ;
        WebView contentView = (WebView)findViewById(R.id.content);
        TextView linkView = (TextView)findViewById(R.id.link);
        TextView pubDateView = (TextView)findViewById(R.id.pubDate);
        ImageView picView = (ImageView) findViewById(R.id.pictureView);


        date = getIntent().getStringExtra("date");
        title = getIntent().getStringExtra("title");
        link = getIntent().getStringExtra("link");
        pubDate = getIntent().getStringExtra("pubDate");
        content = getIntent().getStringExtra("content");
        pic = getIntent().getStringExtra("pic");


        titleView.setText(Html.fromHtml(date+"<br>"+title));


        for(int i=2;titleView.getLineCount()==i;i++){

        }

        try {
            //Replace the spaces with %20 because in lower verson it cashes for some reason.
            pic = "https" + pic.split("http")[1].replaceAll(" ","%20");
        }catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }

        pubDateView.setText(getResources().getString(R.string.published)+pubDate);


        contentView.getSettings().setJavaScriptEnabled(true);
        contentView.loadDataWithBaseURL(null,"<html>" + content+"</html>", "text/html", "utf-8", null);
        linkView.setText(link);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            InputStream is  = (InputStream) new URL(pic).getContent();
            picDrawable = Drawable.createFromStream(is,pic.toString());
            is.close();
            picView.setImageDrawable(picDrawable);

        } catch (IOException e) {
            e.printStackTrace();
        }
        picView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpPicture(picDrawable);
            }
        });
    }

    // Sets up the Menu Buttons and shows them if needed
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(getApplication()).inflate(R.menu.main_menu,menu);
        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.option).setVisible(false);
        menu.findItem(R.id.back).setVisible(true).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    //this is for checking which button is pressed filtered by id
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.back){
            startActivity(new Intent(this,MainActivity.class));
        }
        return true;
    }

    //popUpOption is an alertdialog that shows an ImageView with the given Drawable
    public void popUpPicture(Drawable picDrawable){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.pop_up,null);
        builder.setView(view);
        ImageView popUpPic = view.findViewById(R.id.popUpPic);
        popUpPic.setVisibility(View.VISIBLE);
        popUpPic.setImageDrawable(picDrawable);
        builder.setNeutralButton(getResources().getString(R.string.back), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}
