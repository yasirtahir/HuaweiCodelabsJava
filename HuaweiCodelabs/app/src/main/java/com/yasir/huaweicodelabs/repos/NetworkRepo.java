package com.yasir.huaweicodelabs.repos;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import retrofit.client.UrlConnectionClient;

public class NetworkRepo extends UrlConnectionClient {

    private static OkUrlFactory factory;

    public NetworkRepo() {
        factory = generateDefaultOkUrlFactory();
    }

    private static OkUrlFactory generateDefaultOkUrlFactory() {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(60, TimeUnit.SECONDS);
        client.setReadTimeout(60, TimeUnit.SECONDS);
        return new OkUrlFactory(client);
    }

    @Override
    protected HttpURLConnection openConnection(retrofit.client.Request request) throws IOException {
        return factory.open(new URL(request.getUrl()));
    }
}