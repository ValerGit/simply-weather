package ru.technopark.valery.weather;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private final static String LOG_TAG = MainActivity.class.getSimpleName();
    private NetworkBroadcastReceiver myReceiver;

    static Boolean savedEnableBtnState = true;
    static Boolean savedDisableBtnState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.choose_city).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(MainActivity.this, CurrentCity.class);
                startActivityForResult(intent, 0);

                findViewById(R.id.enable_weather).setEnabled(true);
                findViewById(R.id.disable_weather).setEnabled(false);
            }
        });

        findViewById(R.id.enable_weather).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, R.string.weather_updates_enabled,
                        Toast.LENGTH_SHORT).show();

                final Intent intent = new Intent(MainActivity.this, WeatherIntentService.class);
                intent.setAction(WeatherIntentService.ACTION_UPDATE_CITY_WEATHER);
                startService(intent);

                savedEnableBtnState = false;
                view.setEnabled(false);

                savedDisableBtnState = true;
                findViewById(R.id.disable_weather).setEnabled(true);
            }
        });

        findViewById(R.id.disable_weather).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, R.string.weather_updates_disabled,
                        Toast.LENGTH_SHORT).show();

                final Intent intent = new Intent(MainActivity.this, WeatherIntentService.class);
                intent.setAction(WeatherIntentService.ACTION_STOP_UPDATE_CITY_WEATHER);
                startService(intent);

                savedDisableBtnState = false;
                view.setEnabled(false);

                savedEnableBtnState = true;
                findViewById(R.id.enable_weather).setEnabled(true);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        myReceiver = new NetworkBroadcastReceiver(this);
        this.initBroadcastReceiver(this);

        final ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo == null || activeNetInfo.getState() == NetworkInfo.State.DISCONNECTED) {
            Log.d(LOG_TAG, getString(R.string.no_network_connect));
            final Intent new_intent = new Intent(this, WeatherIntentService.class);
            new_intent.setAction(WeatherIntentService.ACTION_NO_INTERNET);
            this.startService(new_intent);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final Intent intent = new Intent(MainActivity.this, WeatherIntentService.class);
        intent.setAction(WeatherIntentService.ACTION_NEW_CITY_WEATHER);
        startService(intent);

        final Button disableUpdatesBtn = (Button) findViewById(R.id.disable_weather);
        disableUpdatesBtn.setVisibility(View.VISIBLE);
        disableUpdatesBtn.setText(R.string.disable_weather_update);

        final Button enableUpdatesBtn = (Button) findViewById(R.id.enable_weather);
        enableUpdatesBtn.setVisibility(View.VISIBLE);
        enableUpdatesBtn.setText(R.string.enable_weather_update);
    }


    private void initBroadcastReceiver(Context context) {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(WeatherIntentService.ACTION_NEW_CITY_WEATHER);
        filter.addAction(WeatherIntentService.ACTION_DISABLE_ALL);
        filter.addAction(WeatherIntentService.ACTION_ENABLE_ALL);
        LocalBroadcastManager.getInstance(context).registerReceiver(myReceiver, filter);
    }

    public void handleDisable(){
        savedEnableBtnState = findViewById(R.id.enable_weather).isEnabled();
        findViewById(R.id.enable_weather).setEnabled(false);
        savedDisableBtnState = findViewById(R.id.disable_weather).isEnabled();
        findViewById(R.id.disable_weather).setEnabled(false);
        findViewById(R.id.choose_city).setEnabled(false);

        Toast.makeText(MainActivity.this, R.string.check_internet_connection,
                Toast.LENGTH_LONG).show();
        findViewById(R.id.internet_disabled).setVisibility(View.VISIBLE);
    }

    public void handleEnable(){
        findViewById(R.id.enable_weather).setEnabled(savedEnableBtnState);
        findViewById(R.id.disable_weather).setEnabled(savedDisableBtnState);
        findViewById(R.id.choose_city).setEnabled(true);
        findViewById(R.id.internet_disabled).setVisibility(View.GONE);
    }

    public void handleNewWeather(String weatherInfo, String cityName) {
        final TextView weather = (TextView) findViewById(R.id.weather_text);
        weather.setText(weatherInfo);

        final Button currCityBtn = (Button) findViewById(R.id.choose_city);
        currCityBtn.setText(R.string.change_city);

        final TextView city = (TextView) findViewById(R.id.city);
        city.setVisibility(View.VISIBLE);
        city.setText(String.format(getString(R.string.particular_city_weather), cityName));
    }
}


