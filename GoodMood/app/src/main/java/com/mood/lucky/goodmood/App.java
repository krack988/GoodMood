package com.mood.lucky.goodmood;

import android.app.Application;
import android.os.SystemClock;

import java.util.concurrent.TimeUnit;

/**
 * Created by lucky on 12.09.2017.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SystemClock.sleep(TimeUnit.MILLISECONDS.toMillis(700));
    }
}
