package de.nedeco.aufgabe.aufgabenedeco;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    String[] catagory;

    static String searchFilter;

    ArrayList<HashMap<String, String>> list = new ArrayList<>();

    Context context = new ContextWrapper(this);

    SharedPreferences sharedPref;

    SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (searchFilter == null) {
            searchFilter = getResources().getString(R.string.none);
        }


        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.animationin_lr, R.anim.animationout_lr);
        setContentView(R.layout.activity_main);


        sharedPref = this.getPreferences(Context.MODE_PRIVATE);


        final ListView roetgenEventList = findViewById(R.id.roetgenEventList);

        refreshLayout = findViewById(R.id.swipeRefresh);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(MainActivity.this, R.string.reload, Toast.LENGTH_SHORT).show();
                MainActivity.this.recreate();
            }
        });

        List<Entry> entries = new ArrayList();


        try {
            entries = new XmlParsing().execute(sharedPref.getString("RSSLink",getResources().getString(R.string.RSSLink))).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        catagory = new String[entries.size()];

        for (Entry value : entries) {

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("titles", value.getTitle());
            hashMap.put("category", value.getCategory());

            hashMap.put("description", value.getDiscription());
            hashMap.put("link", value.getLink());
            hashMap.put("date", value.getDate());
            hashMap.put("content", value.getContent());
            hashMap.put("pic", value.getPic());
            hashMap.put("error", value.getError());

            list.add(hashMap);

            catagory[entries.indexOf(value)] = value.getCategory();
        }


        TextView emptyView = findViewById(R.id.emptyView);

        switch (list.get(0).get("error")){
            case "java.lang.NullPointerException":
                emptyView.setText(getResources().getString(R.string.emptyXmlList));
                roetgenEventList.setEmptyView(emptyView);
                return;
            case "java.net.IOException":
            case "java.lang.IllegalArgumentException":
                emptyView.setText(getResources().getString(R.string.internetConnectionError));
                roetgenEventList.setEmptyView(emptyView);
                return;
            case "org.xmlpull.v1.XmlPullParserException":
                emptyView.setText(getResources().getString(R.string.linkHasNoRSSFeed));
                roetgenEventList.setEmptyView(emptyView);
                return;
            case "java.net.MalformedURLException":
                emptyView.setText(getResources().getString(R.string.linkIsNotValid));
                roetgenEventList.setEmptyView(emptyView);
                return;
        }


        ArrayList<HashMap<String, String>> data = new ArrayList<>();

        if(searchFilter.equals(getResources().getString(R.string.none))) {
            data = list;
        } else {
            for(HashMap<String, String> a : list) {
                if(a.get("category").toLowerCase().contains(searchFilter.toLowerCase())) {
                    data.add(a);
                }
            }
        }

        roetgenEventList.setAdapter(new SimpleAdapter(this, data, R.layout.list_item, new String[]{"titles", "category"}, new int[]{R.id.item, R.id.subItem}));

        roetgenEventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, AppointmentInfoActivity.class);

                intent.putExtra("content", list.get(i).get("content"));
                intent.putExtra("link", list.get(i).get("link"));
                intent.putExtra("date", list.get(i).get("date"));
                intent.putExtra("pic", list.get(i).get("pic"));

                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(getApplication()).inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.back).setVisible(false);
        menu.findItem(R.id.option).setVisible(true).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(R.id.search).setVisible(true).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                popUpFilter(catagory);
                break;
            case R.id.option:
                popUpOption();
                break;
        }


        return true;
    }


    public void popUpFilter(String[] category) {

        final ArrayList<String> categorySorted = new ArrayList<String>();
        categorySorted.add(0,getResources().getString(R.string.none));
        categorySorted.add(1, category[0]);
        int s = 1;
        for (int i = 0; i != category.length; i++) {
            if (category[i] != "") {
                for (int o = 0; o != category[i].split(",").length; o++) {
                    if (!categorySorted.contains(category[i].split(",")[o])) {
                        s++;
                        categorySorted.add(s, category[i].split(",")[o]);
                    }
                }
            }
        }


        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getResources().getString(R.string.search));
        builder.setNeutralButton(getResources().getString(R.string.back), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        builder.setItems(categorySorted.toArray(new String[categorySorted.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                searchFilter = categorySorted.get(which);
                recreate();
            }
        });
        builder.show();
    }


    public void popUpOption(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.pop_up,null);
        builder.setView(view);
        builder.setTitle(getResources().getString(R.string.setLinkOption));
        final EditText textView = view.findViewById(R.id.popUpTextView);
        textView.setText(sharedPref.getString("RSSLink",getResources().getString(R.string.RSSLink)));
        textView.setSelection(textView.getText().length());
        textView.setVisibility(View.VISIBLE);
        builder.setNeutralButton(getResources().getString(R.string.back), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = sharedPref.edit();
                if(textView.getText().toString().isEmpty()) {
                    editor.remove("RSSLink");
                    editor.commit();
                }else {
                    editor.remove("RSSLink");
                    editor.putString("RSSLink",textView.getText().toString());
                    editor.commit();
                }
                MainActivity.this.recreate();
            }
        });
        builder.show();
    }
}
