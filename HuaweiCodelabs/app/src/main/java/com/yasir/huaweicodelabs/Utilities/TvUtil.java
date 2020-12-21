package com.yasir.huaweicodelabs.Utilities;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;

import static android.content.Context.UI_MODE_SERVICE;

public class TvUtil {

    public static boolean isDirectToTV(Context context) {

        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(UI_MODE_SERVICE);
        if (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {
            AppLog.Debug(TvUtil.class.getSimpleName(), "Running on a TV Device");
            return true;
        } else {
            AppLog.Debug(TvUtil.class.getSimpleName(), "Running on a non-TV Device");
            return false;
        }
    }
}
