package com.yasir.huaweicodelabs.services;

import android.app.NotificationManager;
import android.content.Intent;

import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;
import com.yasir.huaweicodelabs.Utilities.AppLog;

import static com.yasir.huaweicodelabs.Utilities.AppConstant.PUSH_TOKEN;

public class PushService extends HmsMessageService {

    // We will send this new token through broadcast to
    // all the registered classes to usage and display purposes
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        AppLog.Debug(PushService.class.getSimpleName(), "New receive token:" + token);
        sendMessage(token);
    }

    private void sendMessage(String token) {
        Intent intent = new Intent(PUSH_TOKEN);
        intent.putExtra("token", token);
        sendBroadcast(intent);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        AppLog.Debug(PushService.class.getSimpleName(), "New Message from Push Notification:" + remoteMessage);
        // This is called when data type notification is called
        if(remoteMessage.getDataOfMap().size() > 0 && remoteMessage.getDataOfMap().containsKey("body")){
            sendMessage(remoteMessage.getDataOfMap().get("body"));
        }
    }
}
