package ru.technopark.valery.weather;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;

import ru.mail.weather.lib.City;
import ru.mail.weather.lib.Weather;
import ru.mail.weather.lib.WeatherStorage;
import ru.mail.weather.lib.WeatherUtils;


public class WeatherIntentService extends IntentService {
    private final static String LOG_TAG = WeatherIntentService.class.getSimpleName();

    public static final String ACTION_NEW_CITY_WEATHER = "action.NEW_CITY_WEATHER";
    public static final String ACTION_UPDATE_CITY_WEATHER = "action.UPDATE_CITY_WEATHER";
    public static final String ACTION_STOP_UPDATE_CITY_WEATHER = "action.STOP_UPDATE_CITY_WEATHER";

    public final static String EXTRA_WEATHER= "extra.WEATHER";
    public final static String EXTRA_CITY_NAME= "extra.CITY_NAME";

    private WeatherStorage weatherStorage;
    private WeatherUtils weatherUtils;

    public WeatherIntentService() {
        super("WeatherIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            weatherStorage = WeatherStorage.getInstance(WeatherIntentService.this);
            weatherUtils = WeatherUtils.getInstance();
            final String action = intent.getAction();
            switch (action){
                case ACTION_NEW_CITY_WEATHER: {
                    this.handleActionNewWeather();
                    break;
                }
                case ACTION_UPDATE_CITY_WEATHER: {
                    this.handleUpdateWeather();
                    break;
                }
                case ACTION_STOP_UPDATE_CITY_WEATHER: {
                    handleStopUpdateWeather();
                    break;
                }
            }
        }
    }

    private void handleUpdateWeather() {
        final Intent intent = new Intent(this, WeatherIntentService.class);
        intent.setAction(WeatherIntentService.ACTION_NEW_CITY_WEATHER);
        weatherUtils.schedule(this, intent);
    }

    private void handleStopUpdateWeather() {
        final Intent intent = new Intent(this, WeatherIntentService.class);
        intent.setAction(WeatherIntentService.ACTION_NEW_CITY_WEATHER);
        weatherUtils.unschedule(this, intent);
    }

    private void handleActionNewWeather() {
        City thisCity = weatherStorage.getCurrentCity();
        try {
            final Weather weather = weatherUtils.loadWeather(thisCity);
            weatherStorage.saveWeather(thisCity, weather);
            String weatherDescription = String.format("%s°C %s",
                    Integer.toString(weather.getTemperature()), weather.getDescription());

            final Intent intent = new Intent(ACTION_NEW_CITY_WEATHER);
            intent.putExtra(EXTRA_WEATHER, weatherDescription);
            intent.putExtra(EXTRA_CITY_NAME, thisCity.toString());
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }
}
