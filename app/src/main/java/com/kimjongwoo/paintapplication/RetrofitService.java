package com.kimjongwoo.paintapplication;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetrofitService {

    @FormUrlEncoded
    @POST("android/myinfo.php")
    Call<Data> postData(@FieldMap HashMap<String, String> param);

}
