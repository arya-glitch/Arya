package com.example.chat.Notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

public class OreoNotification extends ContextWrapper {
    private String reply;
    private static final  String CHANNEL_ID= "com.example.chat";
    private static final  String CHANNEL_NAME="Chat";


    String KEY_REPLY = "key_reply";



    private NotificationManager notificationManager;


    public OreoNotification(Context base) {
        super(base);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createChannel();
        }
    }


    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel1= new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        channel1.enableLights(true);
        channel1.enableVibration(true);
        channel1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getMessage().createNotificationChannel(channel1);
    }


    public  NotificationManager getMessage(){
        if(notificationManager==null){
            notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return notificationManager;

    }

    @TargetApi(Build.VERSION_CODES.O)
    public NotificationCompat.Builder getOreoNotification(String title, String body,PendingIntent replyIntent,
                                                    PendingIntent pendingIntent, Uri soundUri,String icon){

       // NotificationReceiver notificationReceiver=new NotificationReceiver();

        //NotificationCompat.MessagingStyle messagingStyle =new NotificationCompat.MessagingStyle("Me");

       // NotificationCompat.MessagingStyle.Message notificationmessage= new NotificationCompat.MessagingStyle.Message();

        NotificationCompat.Builder builder= new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(Integer.parseInt(icon))
                .setSound(soundUri)
                .setAutoCancel(true);


        String replyLabel = "Enter your reply here";

        androidx.core.app.RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
                .setLabel(replyLabel)
                .build();

        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                android.R.drawable.sym_action_chat, "Reply", replyIntent)
                .addRemoteInput(remoteInput)
                .setAllowGeneratedReplies(true)
                .build();

        builder.addAction(replyAction);


        return builder;
    }



}
