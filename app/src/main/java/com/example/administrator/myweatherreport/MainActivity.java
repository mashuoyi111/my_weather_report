package com.example.administrator.myweatherreport;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.*;

import static java.lang.Math.round;


public class MainActivity extends AppCompatActivity {

    LocationManager lm;
    OkHttpClient client = new OkHttpClient();
    static final String openweathermapAPIkey = "7ffd8030bebf43ae6ebcfb35c7366b7e";
    static final String APIURL = "http://api.openweathermap.org/data/2.5/forecast?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        setContentView(R.layout.activity_main);

        getLocationAndWeather();
    }

    public void getLocationAndWeather(){
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        String latlon = "lon:" + String.valueOf(longitude) + " lat:" + String.valueOf(latitude);
        TextView latlonText = findViewById(R.id.latlon);
        latlonText.setText(latlon);
        String weatherInfo = "";
        try {
            run( APIURL +
                    "lat=" +
                    String.valueOf(latitude) +
                    "&lon=" + String.valueOf(longitude) +
                    "&appid=" +
                    openweathermapAPIkey);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }


                try {
                    parseWeather(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void parseWeather(String str) throws JSONException {
        TextView weatherInfoText1 = findViewById(R.id.weatherInfo1);
        TextView weatherInfoText2 = findViewById(R.id.weatherInfo2);
        TextView weatherInfoText3 = findViewById(R.id.weatherInfo3);
        TextView weatherInfoText4 = findViewById(R.id.weatherInfo4);
        TextView weatherInfoText5 = findViewById(R.id.weatherInfo5);
        JSONObject obj = new JSONObject(str);
        JSONArray arr = obj.getJSONArray("list");
        System.out.println(arr.length());
        for (int i=0; i< arr.length(); i++){
            if(i%8 == 0){
                JSONObject eachDay = arr.getJSONObject(i);
                double temperature = eachDay.getJSONObject("main").getDouble("temp") - 273.15;
                String eachDayWeather = eachDay.getString("dt_txt").substring(5,10)+ ": " + String.format("%.1f", temperature) + "\u2103";
                if (temperature > 28){
                    eachDayWeather += "LV1: short skirts/shorts";
                } else if (temperature > 24){
                    eachDayWeather += "LV2: long skirts/T-shirts";
                } else if (temperature > 21){
                    eachDayWeather += "LV3: jeans/casual wear";
                } else if (temperature > 18){
                    eachDayWeather += "LV4: jacket/suits";
                } else if (temperature > 15){
                    eachDayWeather += "LV5: overcoat";
                } else if (temperature > 11){
                    eachDayWeather += "LV6: sweater";
                } else {
                    eachDayWeather += "LV7/8: down jacket";
                }
                switch (i/8){
                    case 0: weatherInfoText1.setText(eachDayWeather); break;
                    case 1: weatherInfoText2.setText(eachDayWeather); break;
                    case 2: weatherInfoText3.setText(eachDayWeather); break;
                    case 3: weatherInfoText4.setText(eachDayWeather); break;
                    case 4: weatherInfoText5.setText(eachDayWeather); break;
                }
            }
        }
    }
}
