package com.yasir.huaweicodelabs.Utilities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class YouTubeUtil {
    public static boolean isAppInstalled(String uri, Context context) {
        PackageManager pm = context.getPackageManager();
        boolean installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
            // No Need to print the exception
        }
        return installed;
    }

    public static void watchYoutubeVideo(Context context, String id){
        try {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + id));
            context.startActivity(webIntent);
        } catch (ActivityNotFoundException ex) {
            // No Need to print the exception
        }
    }

    private void playYouTubeVideo(Context context, String videoID){
        if(!YouTubeUtil.isAppInstalled("com.google.android.youtube", context)) {
            // YouTube app not available
            Log.d("YouTube", "Not Available");
            // Open the video in the YouTube browser version
            YouTubeUtil.watchYoutubeVideo(context, videoID);
        } else {
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoID));
            try {
                context.startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                // No Need to print the exception
            }
        }
    }
}