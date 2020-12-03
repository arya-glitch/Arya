package com.example.chat.Notification;

import android.app.NotificationManager;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.BundleCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.chat.MessageActivity;
import com.example.chat.R;
import com.google.firebase.auth.FirebaseAuth;

public class NotificationReceiver extends BroadcastReceiver {
    String message=null;
    private static final  String CHANNEL_ID= "com.example.chat";
    private static final  String CHANNEL_NAME="Chat";

    public NotificationReceiver() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public void onReceive(Context context, Intent intent) {
        String userid = intent.getStringExtra("userId");
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {

            //getting the input value
            CharSequence name = remoteInput.getCharSequence(MessageActivity.KEY_REPLY);
            setMessage(name.toString());
            Long timeStamp = System.currentTimeMillis()/1000;
            MessageActivity messageActivity=new MessageActivity();



            messageActivity.sendMessage(MessageActivity.fuser.getUid(),userid,getMessage(),timeStamp,"default","null");




           /* NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,CHANNEL_ID)
                    .setSmallIcon(R.drawable.arya)
                    .setContentTitle("Reply Sent Successfully");

            NotificationManager notificationManager = (NotificationManager) context.
                    getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(MyFirebaseMessaging.i, mBuilder.build());*/




        }

    }


}
