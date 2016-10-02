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
    public static final String ACTION_DISABLE_ALL= "action.DISABLE_ALL";
    public static final String ACTION_ENABLE_ALL= "action.ENABLE_ALL";
    public static final String ACTION_NO_INTERNET = "action.NO_INTERNET";
    public static final String ACTION_INTERNET_EXIST = "action.INTERNET_OK";

    public final static String EXTRA_WEATHER= "extra.WEATHER";
    public final static String EXTRA_CITY_NAME= "extra.CITY_NAME";

    private WeatherStorage weatherStorage;
    private WeatherUtils weatherUtils;
    private boolean updatesEnabled = false;

    public WeatherIntentService() {
        super("WeatherIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            weatherStorage = WeatherStorage.getInstance(WeatherIntentService.this);
            weatherUtils = WeatherUtils.getInstance();
            final String action = intent.getAction();
            switch (action) {
                case ACTION_NEW_CITY_WEATHER: {
                    this.handleActionNewWeather();
                    break;
                }
                case ACTION_UPDATE_CITY_WEATHER: {
                    this.handleUpdateWeather(true);
                    break;
                }
                case ACTION_STOP_UPDATE_CITY_WEATHER: {
                    handleStopUpdateWeather(true);
                    break;
                }
                case ACTION_NO_INTERNET: {
                    handleNoInternet();
                    break;
                }
                case ACTION_INTERNET_EXIST: {
                    handleInternet();
                    break;
                }
            }
        }
    }

    private void handleNoInternet() {
        if (updatesEnabled) {
            handleStopUpdateWeather(false);
        }
        final Intent intent = new Intent(this, WeatherIntentService.class);
        intent.setAction(WeatherIntentService.ACTION_DISABLE_ALL);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void handleInternet() {
        if (updatesEnabled) {
            handleUpdateWeather(false);
        }
        final Intent intent = new Intent(this, WeatherIntentService.class);
        intent.setAction(WeatherIntentService.ACTION_ENABLE_ALL);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void handleUpdateWeather(Boolean changeEnabledFlag) {
        if (changeEnabledFlag) {
            updatesEnabled = true;
        }
        final Intent intent = new Intent(this, WeatherIntentService.class);
        intent.setAction(WeatherIntentService.ACTION_NEW_CITY_WEATHER);
        weatherUtils.schedule(this, intent);
    }

    private void handleStopUpdateWeather(Boolean changeEnabledFlag) {
        if (changeEnabledFlag) {
            updatesEnabled = false;
        }
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
