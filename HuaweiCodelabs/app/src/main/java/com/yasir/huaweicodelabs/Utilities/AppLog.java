package com.yasir.huaweicodelabs.Utilities;

import android.util.Log;

import com.yasir.huaweicodelabs.Utilities.AppConstant;

public class AppLog {

    public static void Debug(String tag, String msg) {
        if (isEmptyOrNull(msg))
            return;
        if (AppConstant.DEBUG)
            Log.d(tag, msg);
    }

    public static void Error(String tag, String msg) {
        if (isEmptyOrNull(msg))
            return;
        if (AppConstant.DEBUG)
            Log.e(tag, msg);
    }

    private static boolean isEmptyOrNull(String string) {
        if (string == null)
            return true;

        return (string.trim().length() <= 0);
    }
}
