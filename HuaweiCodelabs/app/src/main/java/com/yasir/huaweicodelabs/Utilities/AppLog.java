package com.yasir.huaweicodelabs.Utilities;

import android.util.Log;

import com.huawei.agconnect.crash.AGConnectCrash;

public class AppLog {

    public static void Debug(String tag, String msg) {
        if (isEmptyOrNull(msg))
            return;
        if (AppConstant.DEBUG) {
            Log.d(tag, msg);
            AGConnectCrash.getInstance().log(Log.DEBUG, msg);
        }
    }

    public static void Error(String tag, String msg) {
        if (isEmptyOrNull(msg))
            return;
        if (AppConstant.DEBUG) {
            Log.e(tag, msg);
            AGConnectCrash.getInstance().log(Log.ERROR, msg);
        }
    }

    private static boolean isEmptyOrNull(String string) {
        if (string == null)
            return true;

        return (string.trim().length() <= 0);
    }
}
