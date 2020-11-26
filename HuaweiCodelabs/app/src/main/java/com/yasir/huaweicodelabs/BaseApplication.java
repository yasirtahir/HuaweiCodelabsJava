package com.yasir.huaweicodelabs;

import android.app.Application;

import com.huawei.hms.ads.HwAds;
import com.huawei.hms.analytics.HiAnalyticsTools;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Analytics
        HiAnalyticsTools.enableLog();

        // Initialize the HUAWEI Ads SDK.
        HwAds.init(this);
    }
}
