package com.example.task_app.network_instance;

import com.example.task_app.models.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import androidx.constraintlayout.solver.widgets.Chain;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientInstance {
    private static Retrofit instance;
    private static final String IP_URL = "https://developers.zomato.com/api/v2.1/";

    public static synchronized Retrofit getRetrofitInstance() {
        if (instance == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.level(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .addInterceptor(new MyOkHttpInterceptor()).build();

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            instance = new Retrofit.Builder()
                    .baseUrl(IP_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson)) // converts json obj-> java obj
                    .build();
        }
        return instance;
    }
}

class MyOkHttpInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String userKey = Constants.API_KEY;

        Request newRequest = originalRequest.newBuilder()
                .header("user-key", userKey)
                .build();

        return chain.proceed(newRequest);
    }
}
