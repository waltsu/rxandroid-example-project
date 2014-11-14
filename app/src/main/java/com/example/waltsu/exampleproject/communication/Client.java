package com.example.waltsu.exampleproject.communication;

import android.content.Context;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.lyft.reactivehttp.ReactiveHttpClient;
import com.squareup.okhttp.OkHttpClient;

import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.net.ssl.SSLContext;

import rx.Observable;
import rx.schedulers.Schedulers;

public class Client {
    public static final String LOG_TAG = Client.class.getCanonicalName();
    private static final Client INSTANCE = new Client();
    private ReactiveHttpClient reactiveHttpClient;
    GsonBuilder builder = new GsonBuilder();

    private Client() {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setTransports(Arrays.asList("http/1.1"));
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);
            okHttpClient.setSslSocketFactory(sslContext.getSocketFactory());
        } catch (GeneralSecurityException e) {
            Log.e(LOG_TAG, "System does not have TLS", e);
        }

        reactiveHttpClient = new ReactiveHttpClient(okHttpClient, builder.create(), Schedulers.io(), null, false);
    }

    public static Client getInstance() {
        return INSTANCE;
    }

    public Observable<String> getAsString(Context context, String url) {
        return reactiveHttpClient.create()
            .get(url)
            .observeAsString();
    }

}
