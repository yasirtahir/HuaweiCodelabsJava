package com.yasir.huaweicodelabs.repos;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public abstract class HttpResponseCallback<T> implements Callback<T> {

    public abstract void on200(T responseBody, Response response);

    public abstract void on401(Response response, RetrofitError error);

    public abstract void onFailure(RetrofitError error);

    @Override
    public void success(T object, Response response) {
        if (response.getStatus() == 200) {
            on200(object, response);
        }
    }

    @Override
    public void failure(RetrofitError error) {
        if (error.getResponse() != null) {
            final Response response = error.getResponse();
            if (response.getStatus() == 401) {
                on401(response, error);
            } else {
                onFailure(error);
            }
        } else
            onFailure(error);
    }
}