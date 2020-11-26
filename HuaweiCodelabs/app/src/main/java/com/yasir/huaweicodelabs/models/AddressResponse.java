package com.yasir.huaweicodelabs.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AddressResponse {

    @SerializedName("returnCode")
    private String returnCode;

    @SerializedName("sites")
    private ArrayList<AddressModel> sites;

    public ArrayList<AddressModel> getSites() {
        return sites == null ? new ArrayList<>() : sites;
    }

    public String getReturnCode() {
        return returnCode == null || returnCode.isEmpty() ? "403" : returnCode;
    }

}
