package com.yasir.huaweicodelabs.targets.reservegeo;

import com.google.gson.GsonBuilder;
import com.yasir.huaweicodelabs.repos.NetworkRepo;

import retrofit.RestAdapter;

public class ReserveGeoImplementation {

    private static ReserveGeoTarget reserveGeoTarget;

    public static ReserveGeoTarget getInstanceWithBasicGSonConversion() {
        if (reserveGeoTarget == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setClient(new NetworkRepo())
                    .setEndpoint("https://siteapi.cloud.huawei.com/mapApi/v1")
                    .setConverter(new retrofit.converter.GsonConverter(new GsonBuilder().create()))
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();
            reserveGeoTarget = restAdapter.create(ReserveGeoTarget.class);
        }
        return reserveGeoTarget;
    }
}
