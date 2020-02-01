package com.stove.intelligentstove;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class notify extends Application {
    public static final String CHANNEL_ID = "channel";
    @RequiresApi(api = Build.VERSION_CODES.O)

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }
    private void createNotificationChannels(){
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("This is Channel");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
