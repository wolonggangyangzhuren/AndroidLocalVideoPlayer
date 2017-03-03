package com.yanzhenjie.andserver.sample;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;

/**
 * Created by Administrator on 2017-2-28.
 */

public interface ApiServers {

    @GET("test.m3u8")
    Call<String> getTsUrl();

    @GET("{url}")
    Call<ResponseBody>downLoadFile(String url);
}
