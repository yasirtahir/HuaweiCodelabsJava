package com.yasir.huaweicodelabs.targets.oauth;

import com.yasir.huaweicodelabs.models.TokenModel;
import com.yasir.huaweicodelabs.repos.HttpResponseCallback;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface OAuthTarget {

    @FormUrlEncoded
    @POST("/oauth2/v3/token")
    void validateToken(
            @Field("grant_type") String grant_type,
            @Field("code") String code,
            @Field("client_id") String appID,
            @Field("client_secret") String appSecret,
            @Field("redirect_uri") String redirect_uri,
            HttpResponseCallback<TokenModel> callback
    );

}
