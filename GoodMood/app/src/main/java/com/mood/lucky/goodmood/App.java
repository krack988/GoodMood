package com.mood.lucky.goodmood;

import android.app.Application;
import android.os.SystemClock;

import com.mood.lucky.goodmood.net.BashApi;
import com.vk.sdk.VKSdk;

import java.util.concurrent.TimeUnit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lucky on 12.09.2017.
 */

public class App extends Application {
    private static BashApi bashApi;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(getApplicationContext());
        retrofit = new Retrofit.Builder()
                .baseUrl("http://www.umori.li/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        bashApi = retrofit.create(BashApi.class);

        SystemClock.sleep(TimeUnit.MILLISECONDS.toMillis(700));
    }

    public static BashApi getBashApi(){
        return bashApi;
    }
}
