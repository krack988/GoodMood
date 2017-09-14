package com.mood.lucky.goodmood.net;

import com.mood.lucky.goodmood.model.BashModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by lucky on 14.09.2017.
 */

public interface BashApi {
    @GET("/api/get/")
    Call<List<BashModel>> getData(@Query("name") String resourceName , @Query("num") int count);
}
