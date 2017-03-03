package com.yanzhenjie.andserver.sample;


import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Administrator on 2017-1-3.
 */

public class HttpRequestHelper {
    private static String IDEA_URL = "http://47.93.114.18/";
    private static ApiServers apiServes;
    private HttpRequestHelper(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IDEA_URL +"/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiServes = retrofit.create(ApiServers.class);
    }

    public ApiServers getApiServes()
    {
        return apiServes;
    }

    private static class SingleHolder
    {
        private static final HttpRequestHelper requestHelper = new HttpRequestHelper();
    }

    public static HttpRequestHelper getInstance()
    {
        return SingleHolder.requestHelper;
    }
}
