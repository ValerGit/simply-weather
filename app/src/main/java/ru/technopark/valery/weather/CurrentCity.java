package ru.technopark.valery.weather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ru.mail.weather.lib.City;
import ru.mail.weather.lib.WeatherStorage;


public class CurrentCity extends AppCompatActivity {
    private WeatherStorage weatherStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherStorage = WeatherStorage.getInstance(this);
        setContentView(R.layout.activity_current_city);

        final Button p1_button = (Button)findViewById(R.id.first_city);
        p1_button.setText(City.SILENT_HILL.toString());
        findViewById(R.id.first_city).setOnClickListener(new View.OnClickListener(){
            final City thisCity = City.SILENT_HILL;
            @Override
            public void onClick(View view) {
                weatherStorage.setCurrentCity(thisCity);
                finish();
            }
        });

        final Button p2_button = (Button)findViewById(R.id.second_city);
        p2_button.setText(City.SOUTH_PARK.toString());
        findViewById(R.id.second_city).setOnClickListener(new View.OnClickListener(){
            final City thisCity = City.SOUTH_PARK;
            @Override
            public void onClick(View view) {
                weatherStorage.setCurrentCity(thisCity);
                finish();
            }
        });

        final Button p3_button = (Button)findViewById(R.id.third_city);
        p3_button.setText(City.RACCOON_CITY.toString());
        findViewById(R.id.third_city).setOnClickListener(new View.OnClickListener(){
            final City thisCity = City.RACCOON_CITY;
            @Override
            public void onClick(View view) {
                weatherStorage.setCurrentCity(thisCity);
                finish();
            }
        });

        final Button p4_button = (Button)findViewById(R.id.fourth_city);
        p4_button.setText(City.SPRINGFIELD.toString());
        findViewById(R.id.fourth_city).setOnClickListener(new View.OnClickListener(){
            final City thisCity = City.SPRINGFIELD;
            @Override
            public void onClick(View view) {
                weatherStorage.setCurrentCity(thisCity);
                finish();
            }
        });

        final Button p5_button = (Button)findViewById(R.id.fith_city);
        p5_button.setText(City.VICE_CITY.toString());
        findViewById(R.id.fith_city).setOnClickListener(new View.OnClickListener(){
            final City thisCity = City.VICE_CITY;
            @Override
            public void onClick(View view) {
                weatherStorage.setCurrentCity(thisCity);
                finish();
            }
        });
    }
}
