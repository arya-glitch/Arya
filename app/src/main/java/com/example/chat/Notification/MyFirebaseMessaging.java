package com.example.chat.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.chat.MessageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    public static int i=0;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String sented = remoteMessage.getData().get("sented");

        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();


        if(firebaseUser!=null && sented.equals(firebaseUser.getUid())){


            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                sendOreoNotification(remoteMessage);
            }else {
                sendNotification(remoteMessage);
            }

            }

    }

    private void sendOreoNotification(RemoteMessage remoteMessage) {
        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");


        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int j= Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent01=new Intent(getApplicationContext(), MessageActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("userid",user);
        bundle.putString("imageurl", "default");
        bundle.putString("videourl","default");
        bundle.putString("message", "");
        intent01.putExtras(bundle);
        intent01.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent= PendingIntent.getActivity(this,j,intent01,PendingIntent.FLAG_ONE_SHOT);

        Intent intent02=new Intent(getApplicationContext(),NotificationReceiver.class);
        Bundle bundle1=new Bundle();
        bundle1.putString("userId",user);
        intent02.putExtras(bundle1);
        //intent02.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent replyIntent=PendingIntent.getBroadcast(this,j,intent02,PendingIntent.FLAG_UPDATE_CURRENT);


        Uri defaultsound = RingtoneManager.getDefaultUri(Notification.DEFAULT_SOUND);

        OreoNotification oreoNotification= new OreoNotification(this);
        NotificationCompat.Builder builder=oreoNotification.getOreoNotification(title,body,replyIntent,pendingIntent,
                defaultsound,icon);

         i=0;
        if(j>0){
            i=j;
        }

        oreoNotification.getMessage().notify(i,builder.build());

    }







    private void sendNotification(RemoteMessage remoteMessage) {

        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");


        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int j= Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent =new Intent(this, NotificationReceiver.class);
        Bundle bundle=new Bundle();
        bundle.putString("userid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent= PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification.Builder builder=new Notification.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultsound)
                .setContentIntent(pendingIntent);

        NotificationManager noti =(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        int i=0;
        if(j>0){
            i=j;
        }

        noti.notify(i,builder.build());

    }
}
