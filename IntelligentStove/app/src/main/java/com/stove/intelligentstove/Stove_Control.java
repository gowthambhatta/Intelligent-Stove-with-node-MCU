package com.stove.intelligentstove;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.Firebase;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.core.Context;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.widget.ImageView;
import static android.widget.Toast.*;
import static com.stove.intelligentstove.notify.CHANNEL_ID;

public class Stove_Control extends AppCompatActivity {
    private Button low;
    private Button sim;
    private Button high;
    private ImageView home;
    private Firebase mRef;
    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_control);
        notificationManager = NotificationManagerCompat.from(this);
        low = (Button) findViewById(R.id.low);
        sim = (Button) findViewById(R.id.sim);
        high = (Button) findViewById(R.id.high);
        home = (ImageView) findViewById(R.id.home);
        mRef = new Firebase("https://intelligentstove.firebaseio.com/");// DataBase Profile Link



        mRef.addValueEventListener(new ValueEventListener() {
            private DataSnapshot dataSnapshot;
            private String value;

            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                String servo1 = dataSnapshot.child("servo").getValue().toString();

                String mq6 = dataSnapshot.child("MQ6").getValue().toString();
                if("0".equalsIgnoreCase(mq6)){
                    low.setEnabled(false);
                    sim.setEnabled(false);
                    high.setEnabled(false);
                    sendonChannel();
                    dataSnapshot.getRef().child("servo").setValue("20");
                    Toast toast= makeText(getApplicationContext(),"Stove Leakage Detected!", LENGTH_SHORT);
                    toast.show();
                }else{
                    low.setEnabled(true);
                    sim.setEnabled(true);
                    high.setEnabled(true);
                    if("20".equalsIgnoreCase(servo1)){
                        home.setImageResource(R.drawable.stove_off);
                    }
                    else if("110".equalsIgnoreCase(servo1)){
                        home.setImageResource(R.drawable.high);
                    }
                    else if("180".equalsIgnoreCase(servo1)){
                        home.setImageResource(R.drawable.sim);
                    }
                   // Toast toast= makeText(getApplicationContext(),"Back to user mode", LENGTH_SHORT);
                    //toast.show();
                    sim.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            updateServo(dataSnapshot,"180");
                            updateStove(dataSnapshot,"ON");
                            home.setImageResource(R.drawable.sim);
                            Toast toast= makeText(getApplicationContext(),"Stove is at SIM state", LENGTH_SHORT);
                            toast.show();
                        }
                    });
                    low.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            updateServo(dataSnapshot,"20");
                            updateStove(dataSnapshot,"OFF");
                            home.setImageResource(R.drawable.stove_off);
                            Toast toast= makeText(getApplicationContext(),"Stove is at OFF state", LENGTH_SHORT);
                            toast.show();
                        }
                    });
                    high.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            updateServo(dataSnapshot,"110");
                            updateStove(dataSnapshot,"ON");
                            home.setImageResource(R.drawable.high);
                            Toast toast= makeText(getApplicationContext(),"Stove is at HIGH State", LENGTH_SHORT);
                            toast.show();
                        }
                    });


                }

            }



            public  void updateServo(DataSnapshot dataSnapshot, String value){
                dataSnapshot.getRef().child("servo").setValue(value);
            }
            public  void updateStove(DataSnapshot dataSnapshot, String value){
                this.dataSnapshot = dataSnapshot;
                this.value = value;
                dataSnapshot.getRef().child("stoveStatus").setValue(value);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    public void sendonChannel(){
        Intent notifyIntent = new Intent(this, Stove_Control.class);
// Set the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
// Create the PendingIntent
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle("Intelligent Stove");
        builder.setContentText("Gas Leakage Detected");
        builder.setSmallIcon(R.drawable.notify);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setCategory(NotificationCompat.CATEGORY_ALARM);
        builder.setContentIntent(notifyPendingIntent);
        Notification notification = builder.build();

        notificationManager.notify(1, notification);
    }
}
