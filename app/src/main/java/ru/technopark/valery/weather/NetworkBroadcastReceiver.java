package ru.technopark.valery.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkBroadcastReceiver extends BroadcastReceiver {
    private final static String LOG_TAG = NetworkBroadcastReceiver.class.getSimpleName();
    public final static String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    private MainActivity activity;

    public NetworkBroadcastReceiver() {
        super();
    }

    public NetworkBroadcastReceiver(MainActivity activity) {
        super();
        this.activity = activity;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(LOG_TAG, "Network connectivity change");
        final String action = intent.getAction();

        switch (action) {
            case ACTION_CONNECTIVITY_CHANGE: {
                ConnectivityManager connectivityManager = (ConnectivityManager)
                        context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

                if (activeNetInfo != null && activeNetInfo.getState() == NetworkInfo.State.CONNECTED) {
                    Log.i(LOG_TAG, String.format("Network %s connected", activeNetInfo.getTypeName()));
                    final Intent new_intent = new Intent(context, WeatherIntentService.class);
                    new_intent.setAction(WeatherIntentService.ACTION_INTERNET_EXIST);
                    context.startService(new_intent);
                } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                    Log.d(LOG_TAG, "There's no network connectivity");
                    final Intent new_intent = new Intent(context, WeatherIntentService.class);
                    new_intent.setAction(WeatherIntentService.ACTION_NO_INTERNET);
                    context.startService(new_intent);
                }
                break;
            }
            case WeatherIntentService.ACTION_NEW_CITY_WEATHER: {
                final String weatherInfo = intent.getStringExtra(WeatherIntentService.EXTRA_WEATHER);
                final String cityName = intent.getStringExtra(WeatherIntentService.EXTRA_CITY_NAME);
                activity.handleNewWeather(weatherInfo, cityName);
                break;
            }
            case WeatherIntentService.ACTION_DISABLE_ALL: {
                activity.handleDisable();
                break;
            }
            case WeatherIntentService.ACTION_ENABLE_ALL: {
                activity.handleEnable();
                break;
            }
        }
    }
}

