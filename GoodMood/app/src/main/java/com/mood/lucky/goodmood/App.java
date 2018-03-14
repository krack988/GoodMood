package com.mood.lucky.goodmood;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.SystemClock;
import com.vk.sdk.VKSdk;
import java.util.concurrent.TimeUnit;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lucky on 12.09.2017.
 */

public class App extends Application {
    private Retrofit retrofit;
    private Bitmap bitmap;
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(getApplicationContext());
        retrofit = new Retrofit.Builder()
                .baseUrl("http://www.umori.li/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SystemClock.sleep(TimeUnit.MILLISECONDS.toMillis(700));

        instance = this;
    }

    public static App getInstance (){
        return instance;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
    }

}
