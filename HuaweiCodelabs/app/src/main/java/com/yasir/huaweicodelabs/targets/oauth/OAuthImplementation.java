package com.yasir.huaweicodelabs.targets.oauth;

import com.google.gson.GsonBuilder;
import com.yasir.huaweicodelabs.repos.NetworkRepo;

import retrofit.RestAdapter;

public class OAuthImplementation {

    private static OAuthTarget oAuthTarget;

    public static OAuthTarget getInstance() {
        if (oAuthTarget == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setClient(new NetworkRepo())
                    .setEndpoint("https://oauth-login.cloud.huawei.com/")
                    .setConverter(new retrofit.converter.GsonConverter(new GsonBuilder().create()))
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();
            oAuthTarget = restAdapter.create(OAuthTarget.class);
        }
        return oAuthTarget;
    }

}
