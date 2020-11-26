package com.yasir.huaweicodelabs.models;

import com.google.gson.annotations.SerializedName;

public class AddressModel {

    @SerializedName("formatAddress")
    private String formatAddress;

    public String getFormatAddress() {
        return formatAddress == null ? "NA" : formatAddress.trim();
    }

    public void setFormatAddress(String formatAddress) {
        this.formatAddress = formatAddress;
    }

}
