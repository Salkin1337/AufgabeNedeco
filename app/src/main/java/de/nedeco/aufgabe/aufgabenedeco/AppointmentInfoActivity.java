package de.nedeco.aufgabe.aufgabenedeco;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

public class AppointmentInfoActivity extends AppCompatActivity {

    String link;
    String date;
    String content;
    String pic;
    Drawable picDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.appointment_info);

        overridePendingTransition(R.anim.animationin_rl, R.anim.animationout_rl);


        WebView contentView = findViewById(R.id.content);
        TextView linkView = findViewById(R.id.link);
        TextView dateView = findViewById(R.id.date);
        ImageView picView = findViewById(R.id.pictureView);


        link = getIntent().getStringExtra("link");
        date = getIntent().getStringExtra("date");
        content = getIntent().getStringExtra("content");
        pic = getIntent().getStringExtra("pic");


        try {
            pic = "https" + pic.split("http")[1].replaceAll(" ","%20");
        }catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }


        dateView.setText(date);
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
                popUpPicture();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(getApplication()).inflate(R.menu.main_menu,menu);
        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.option).setVisible(false);
        menu.findItem(R.id.back).setVisible(true).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.back){
            startActivity(new Intent(this,MainActivity.class));
        }
        return true;
    }

    public void popUpPicture(){

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
