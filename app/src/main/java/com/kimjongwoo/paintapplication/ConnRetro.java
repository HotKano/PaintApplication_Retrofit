package com.kimjongwoo.paintapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class ConnRetro {

    private Context context;
    private HashMap<String, String> input;
    private String url;

    ConnRetro(String url, Context context, HashMap<String, String> input) {
        this.url = url;
        this.context = context;
        this.input = input;
    }

    //retrofit 접속
    void connection_retrofit() {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RetrofitService retrofitService = retrofit.create(RetrofitService.class);


            Call<Data> call = retrofitService.postData(input);
            call.enqueue(new Callback<Data>() {
                @Override
                public void onResponse(@NonNull Call<Data> call, @NonNull Response<Data> response) {
                    if (response.isSuccessful()) {
                        Data dataResponse = response.body();
                        if (dataResponse != null) {
                            String result = dataResponse.response.getResult();
                            String age = dataResponse.content.getAge();
                            String name = dataResponse.content.getName();
                            Toast.makeText(context, result + " " + name + " " + age, Toast.LENGTH_SHORT).show();
                        } else {
                            alert();
                        }
                    } else {
                        alert();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Data> call, @NonNull Throwable t) {
                    Log.d("ani_test", "onFailure " + call.request().url().toString());
                    alert();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void alert() {
        AlertDialog.Builder alertDialog_Builder = new AlertDialog.Builder(context);
        alertDialog_Builder.setTitle(R.string.app_name);

        alertDialog_Builder.setMessage(R.string.alert_msg)
                .setCancelable(false)
                .setPositiveButton(R.string.alert_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((LauncherActivity) context).finish();
                    }
                });

        AlertDialog alertDialog = alertDialog_Builder.create();
        alertDialog.show();
    }
}
