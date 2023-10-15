package com.example.healthhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MedicinesActivity extends AppCompatActivity {
    private void render(DataSnapshot ds, boolean redone, DatabaseReference dataRef) {
        Gson g = new Gson();

        try {
            JSONObject j = new JSONObject(g.toJson(ds.getValue()));
            Iterator<String> keys = j.keys();
            LinearLayout ltt = findViewById(R.id.ltt);
            ltt.removeAllViews();
            while (keys.hasNext()) {
                LinearLayout ll = new LinearLayout(this);

                ll.setOrientation(LinearLayout.VERTICAL);
                String key = keys.next();
                TextView medicineName = new TextView(this);
                medicineName.setTextSize(24);
                medicineName.setTypeface(null, Typeface.BOLD);
                medicineName.setText(key);
                ll.addView(medicineName);
                JSONObject times = j.getJSONObject(key);
                Iterator<String> itimes = times.keys();
                String pattern = "yyyy/MM/dd";

// Create an instance of SimpleDateFormat used for formatting
// the string representation of date according to the chosen pattern
                DateFormat df = new SimpleDateFormat(pattern);

// Get the today date using Calendar object.
                Date today = Calendar.getInstance().getTime();
// Using DateFormat format method we can create a string
// representation of a date with the defined format.
                String todayAsString = df.format(today);
                List<String> tlist = new ArrayList<>();
                while (itimes.hasNext()) {
                    String time = itimes.next();
                    TextView timeOfDay = new TextView(this);
                    timeOfDay.setTextSize(15);
                    timeOfDay.setTypeface(null, Typeface.ITALIC);
                    timeOfDay.setText(time + ": " + times.getString(time));
                    ll.addView(timeOfDay);
                    tlist.add(todayAsString + " " + times.getString(time));

                }
                Button rem = new Button(this);
                rem.setText("Add reminder");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                rem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (String s : tlist) {
                            System.out.println(s);
                            Intent intent = new Intent(Intent.ACTION_INSERT);
                            intent.setData(CalendarContract.Events.CONTENT_URI);
                            intent.putExtra(CalendarContract.Events.TITLE, key);
                            intent.putExtra(CalendarContract.Events.RRULE, "FREQ=DAILY;");


                            Date date = null;
                            try {
                                date = sdf.parse(s);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            long millis = date.getTime();

                            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, millis);
                            //intent.putExtra(CalendarContract.Events.DESCRIPTION, "DSDKFJLSFJL");
                            //intent.putExtra(CalendarContract.Events.EVENT_LOCATION,binding.etLoc.getText().toString())
                            //intent.putExtra(CalendarContract.Events.ALL_DAY,"true")
                            //intent.putExtra(Intent.EXTRA_EMAIL,"BASIC");

                            if(intent.resolveActivity(getPackageManager()) != null) {
                                startActivity(intent);
                            }
                        }

                    }
                });
                Button b = new Button(this);
                b.setText("Delete");
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dataRef.child(key).removeValue();
                    }
                });

                Space space1 = new Space(this);
                space1.setMinimumHeight(40);
                ll.addView(space1);

                ll.addView(b);
                Space space2 = new Space(this);
                space2.setMinimumHeight(40);
                ll.addView(rem);
                ltt.addView(ll);
                Space space = new Space(this);
                space.setMinimumHeight(80);
                ll.addView(space);
            }
           System.out.println(j);
        } catch (JSONException e) {
            System.out.println("OK");
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicines);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference dataRef = db.getReference(firebaseAuth.getCurrentUser().getUid()).child("Medicines");
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                render(snapshot, true, dataRef);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dataRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                render(task.getResult(), false, dataRef);
            }
        });
        setContentView(R.layout.activity_medicines);
    }
}