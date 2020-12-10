package com.yasir.huaweicodelabs.Utilities;

import android.content.Context;

import com.google.android.gms.common.GoogleApiAvailability;
import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiAvailability;

public class HmsGmsUtil {

    public static boolean isHmsAvailable(Context context){
        boolean isAvailable = false;
        if(context != null){
            int result = HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(context);
            isAvailable = ConnectionResult.SUCCESS == result;
        }
        AppLog.Debug(HmsGmsUtil.class.getSimpleName(), "isHmsAvailable: " + isAvailable);
        return isAvailable;
    }

    public static boolean isGmsAvailable(Context context){
        boolean isAvailable = false;
        if(context != null){
            int result = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
            isAvailable = ConnectionResult.SUCCESS == result;
        }
        AppLog.Debug(HmsGmsUtil.class.getSimpleName(), "isHmsAvailable: " + isAvailable);
        return isAvailable;
    }

    public static boolean isHMSOnly(Context context){
        return !isGmsAvailable(context) && isHmsAvailable(context);
    }
}
