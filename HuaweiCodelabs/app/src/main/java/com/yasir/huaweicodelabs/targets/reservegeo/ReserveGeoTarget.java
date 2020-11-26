package com.yasir.huaweicodelabs.targets.reservegeo;

import com.yasir.huaweicodelabs.models.AddressResponse;
import com.yasir.huaweicodelabs.repos.HttpResponseCallback;

import retrofit.http.Body;
import retrofit.http.POST;

public interface ReserveGeoTarget {

    // https://siteapi.cloud.huawei.com/mapApi/v1/siteService/reverseGeocode?key=API_KEY

    @POST("/siteService/reverseGeocode?key=CgB6e3x95Qpt4fbjKRPXb0TEz3L3lBj6SuRkQ1HVDl462WVaWGSPOUS0w+VG9PrJ395o3IN58fGF/X4jPWbvhVKS")
    void address (
            @Body Object jsonBody,
            HttpResponseCallback<AddressResponse> callback
    );
}