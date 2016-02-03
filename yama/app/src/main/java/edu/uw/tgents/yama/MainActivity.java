package edu.uw.tgents.yama;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Map<String, ArrayList<Text>> texts;
    private int currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SendActivity.class);
                startActivity(intent);
            }
        });

        if (texts == null) {
            texts = new HashMap<String, ArrayList<Text>>();
            populateTexts();
        }

        currentView = 0;
        final AdapterView listView = (AdapterView) findViewById(R.id.texts);
        listView.setAdapter(getContactAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentView == 0) {
                    String phone = (String) parent.getItemAtPosition(position);
                    if (texts.containsKey(phone)) {
                        listView.setAdapter(getTextAdapter(phone));
                        currentView = 1;
                        ((TextView) findViewById(R.id.title)).setText(phone);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    }
                }
            }
        });

    }

    private ArrayAdapter<String> getContactAdapter() {
        ArrayList<String> list = new ArrayList<String>();
        list.addAll(texts.keySet());
        return new ArrayAdapter<String>(this, R.layout.list_item, R.id.txtItem, list);
    }

    private ArrayAdapter<Text> getTextAdapter(String num) {
        ArrayList<Text> list = new ArrayList<Text>();
        list.addAll(texts.get(num));
        Collections.sort(list);
        return new ArrayAdapter<Text>(this, R.layout.list_item, R.id.txtItem, list);
    }

    private void populateTexts() {
        Uri uri = Telephony.Sms.Inbox.CONTENT_URI;
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            String address = c.getString(c.getColumnIndexOrThrow("address"));
            String body = c.getString(c.getColumnIndexOrThrow("body"));
            long date = c.getLong(c.getColumnIndexOrThrow("date"));
            if (!texts.containsKey(address)) {
                texts.put(address, new ArrayList<Text>());
            }
            texts.get(address).add(new Text(date, body));
            c.moveToNext();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (currentView == 1) {
            currentView = 0;
            AdapterView listView = (AdapterView) findViewById(R.id.texts);
            listView.setAdapter(getContactAdapter());
            ((TextView) findViewById(R.id.title)).setText("Your Messages:");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else {
            super.onBackPressed();
        }
    }

    class Text implements Comparable<Text>{
        long time;
        String date;
        String body;

        public Text(long d, String s) {
//            Date temp = new Date(d/1000);
//            date = new SimpleDateFormat("MM/dd/yyyy hh:mm aa").format(temp);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
            date = sdf.format(d);
            time = d;
            body = s;
        }

        @Override
        public String toString() {
            return date + "\n" + body;
        }

        @Override
        public int compareTo(Text another) {
            return (int)(another.time/10000 - this.time/10000);
        }
    }
}
