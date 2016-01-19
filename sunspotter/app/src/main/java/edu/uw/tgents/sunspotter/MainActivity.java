package edu.uw.tgents.sunspotter;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "Sunspotter";
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new ArrayAdapter<String>(
                this, R.layout.listpanel, R.id.txtItem, new ArrayList<String>(0));

        AdapterView listView = (AdapterView)findViewById(R.id.listPanel);
        listView.setAdapter(adapter);

        final EditText text = (EditText) findViewById(R.id.search);
        final Button searchBtn = (Button) findViewById(R.id.button);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                Log.v(TAG, "Button was pressed");
                String zipcode = text.getText().toString();

                WeatherTask task = new WeatherTask();
                task.execute(zipcode);
            }
        });
    }

    private class WeatherTask extends AsyncTask<String, Void, ArrayList<String>> {
        String location = null;
        protected ArrayList<String> doInBackground(String... params) {
            String zip = params[0];

            Uri.Builder uri = new Uri.Builder();
            uri.scheme("http")
                    .authority("api.openweathermap.org")
                    .appendPath("/data/2.5/forecast")
                    .appendQueryParameter("zip", zip)
                    .appendQueryParameter("units", "imperial")
                    .appendQueryParameter("appid", BuildConfig.OPEN_WEATHER_MAP_API_KEY);

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            ArrayList<String> forecast = new ArrayList<String>();

            try {
                URL url = new URL(uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                if (buffer.length() == 0) {
                    return null;
                }
                String results = buffer.toString();
                JSONObject obj = new JSONObject(buffer.toString());
                location = obj.getJSONObject("city").getString("name");
                JSONArray list = obj.getJSONArray("list");
                for(int i = 0; i < list.length(); i++){
                    JSONObject report = list.getJSONObject(i);

                    //get temperature
                    Double temp = report.getJSONObject("main").getDouble("temp");

                    //get time
                    SimpleDateFormat sdf = new SimpleDateFormat("ccc HH:mma", Locale.US);
                    Date time = new Date(report.getLong("dt")*1000);
                    String formatTime = sdf.format(time);

                    //get weather
                    String weat = report.getJSONArray("weather").getJSONObject(0).getString("main");
                    if(weat.equals("Clear")){
                        weat = "SUN!";
                    }else{
                        weat = "No Sun on";
                    }

                    String addThis = weat + " " + formatTime + " - " + temp + "°F";
                    //Log.v(TAG, addThis);
                    forecast.add(addThis);
                }

            } catch (IOException e) {
                Log.v(TAG, e.toString());
                return null;
            } catch (JSONException e) {
                Log.v(TAG, e.toString());
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                    }
                }
            }

            return forecast;
        }

        protected void onPostExecute(ArrayList<String> forecast) {
            String firstSun = null;
            boolean foundSun = false;
            if (forecast != null) {
                adapter.clear();
                for (String temp : forecast) {
                    adapter.add(temp);
                    if(temp.startsWith("SUN!") && !foundSun){
                        firstSun = location +" will have Sun on " + temp.substring(5,16) + "!";
                        foundSun = !foundSun;
                    }
                }

                ViewStub stub = (ViewStub) findViewById(R.id.weatherStub);
                if(stub != null){
                    View inflated = stub.inflate();
                }

                if(foundSun){
                    ((ImageView) findViewById(R.id.sunny)).setImageResource(R.mipmap.sunicon);
                    ((TextView) findViewById(R.id.tellem)).setText(firstSun);
                }else{
                    ((ImageView) findViewById(R.id.sunny)).setImageResource(R.mipmap.sadicon);
                    ((TextView) findViewById(R.id.tellem)).setText("No Sun found in " + location + "...");
                }

            }else{
                Toast.makeText(getApplicationContext(), "Please enter a valid Zip Code", Toast.LENGTH_LONG).show();
            }
        }
    }

}
