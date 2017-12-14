package de.nedeco.aufgabe.aufgabenedeco;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    String[] link;
    String[] date;
    String[] content;
    String[] pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        ListView roetgenEventList = (ListView) findViewById(R.id.roetgenEventList);

        List<Entry> entries = new ArrayList();
        try {
            entries = new xmlParsing().execute("https://www.datefix.de/js/kalender/rss/5095").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (entries !=null) {

        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Internet connection found").setNeutralButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    MainActivity.this.recreate();
                }
            });
            builder.create().show();
            return;
        }


        final String[] titles = new String[entries.size()];
        int indexT = 0;
        for (Entry value : entries) {
            titles[indexT] =  value.getTitle();
            indexT++;
        }


        String[] destcription = new String[entries.size()];
        int indexD = 0;
        for (Entry value : entries) {
            destcription[indexD] =  value.getDiscription();
            indexD++;
        }


        link = new String[entries.size()];
        int indexL = 0;
        for (Entry value : entries) {
            link[indexL] =  value.getLink();
            indexL++;
        }


        date = new String[entries.size()];
        int indexDa = 0;
        for (Entry value : entries) {
            date[indexDa] =  value.getDate();
            indexDa++;
        }


        content = new String[entries.size()];
        int indexC = 0;
        for (Entry value : entries) {
            content[indexC] =  value.getContent();
            indexC++;
        }


        pic = new String[entries.size()];
        int indexP = 0;
        for (Entry value : entries) {
            pic[indexP] =  value.getPic();
            indexP++;
        }


        roetgenEventList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, R.id.item, titles));
        final List<Entry> finalEntries = entries;
        roetgenEventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this,appointmentInfo.class);

                intent.putExtra("content", content[i]);
                intent.putExtra("link", link[i]);
                intent.putExtra("date", date[i]);
                intent.putExtra("pic", pic[i]);

                startActivity(intent);
            }
        });

    }
}
