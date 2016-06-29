package com.fxecocal.free.controller.push;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.fxecocal.free.R;
import com.fxecocal.free.controller.MainActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;


public class GcmIntentService extends IntentService {

    int i = 0;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty() && GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            setNotificationData(extras);
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    public void setNotificationData(Bundle data) {
        parseMessage(data);
        if (!message.equals("")) {

            sendNotification();
        }
    }


    private void sendNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (type.equals("activity") ||
                type.equals("receive_invite") ||
                type.equals("accept_friend") ||
                type.equals("taged")) {
//            intent.putExtra("page_num", 6);
            intent.putExtra("type", "activity");
        } else if (type.equals("white")) {
            intent.putExtra("type", "message");
        }

        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, i++,
                intent,  PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setSound(defaultSoundUri)
                        .setContentText(message);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(0, mBuilder.build());
    }

    String id, type, message, conversation_id, receiver_id;
    private String parseMessage(Bundle data){
        id = "";
        type = "";
        message = "";
        conversation_id = "";
        receiver_id = "";
        if(data.containsKey("liked_post")){
            message = data.getString("liked_post");
            String[] str = message.split("_like_post_");
            message = str[0];
            receiver_id = str[1];
            type = "activity";
        }
        return message;
    }
//    private void localBroadCast(PushModel pushModel) {
//        Intent intentNewPush = new Intent("pushData");
//        intentNewPush.putExtra(Constant.PUSH_DATA, pushModel);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intentNewPush);
//    }
}