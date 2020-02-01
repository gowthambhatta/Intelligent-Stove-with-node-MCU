package com.stove.intelligentstove;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.Firebase;
import com.google.firebase.analytics.FirebaseAnalytics;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

public class Stove_Environ extends AppCompatActivity {
    private TextView mtextView;
    private TextView mtextView1;
    private Firebase mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_environ);


        mtextView = (TextView) findViewById(R.id.temp);
        mtextView1 = (TextView) findViewById(R.id.humit);
        mRef = new Firebase("https://intelligentstove.firebaseio.com/");  // DataBase Profile Link

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String temp = dataSnapshot.child("temperature").getValue().toString();
                String humid = dataSnapshot.child("humidity").getValue().toString();
                String temper = "Temperature Around the System\n" + temp + " deg Celsius";
                String Humidi = "Humidity Around the System\n" + humid + " %";
                mtextView.setText(temper);
                mtextView1.setText(Humidi);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}