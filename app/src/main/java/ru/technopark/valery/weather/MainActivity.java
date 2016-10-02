package ru.technopark.valery.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initBroadcastReceiver(this);
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

                view.setEnabled(false);
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

                view.setEnabled(false);
                findViewById(R.id.enable_weather).setEnabled(true);
            }
        });

    }


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

        LocalBroadcastManager.getInstance(context).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                if (WeatherIntentService.ACTION_NEW_CITY_WEATHER.equals(intent.getAction())){
                    final String weatherInfo = intent.getStringExtra(WeatherIntentService.EXTRA_WEATHER);
                    final String cityName = intent.getStringExtra(WeatherIntentService.EXTRA_CITY_NAME);
                    handleNewWeather(weatherInfo, cityName);
                }
            }
        }, filter);
    }


    private void handleNewWeather(String weatherInfo, String cityName) {
        final TextView weather = (TextView) findViewById(R.id.weather_text);
        weather.setText(weatherInfo);

        final Button currCityBtn = (Button) findViewById(R.id.choose_city);
        currCityBtn.setText(R.string.change_city);

        final TextView city = (TextView) findViewById(R.id.city);
        city.setVisibility(View.VISIBLE);
        city.setText(String.format(getString(R.string.particular_city_weather), cityName));
    }
}
