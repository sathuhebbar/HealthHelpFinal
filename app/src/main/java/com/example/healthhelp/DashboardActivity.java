package com.example.healthhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import org.json.JSONException;
import org.json.JSONObject;


public class DashboardActivity extends AppCompatActivity {
    private void render(DataSnapshot ds, boolean redone) {
        Gson g = new Gson();

        try {
            if (redone) {
                Toast toast = Toast.makeText(this /* MyActivity */, "IoT data received", Toast.LENGTH_SHORT);
                toast.show();
            }
            JSONObject j = new JSONObject(g.toJson(ds.getValue()));
            TextView name = findViewById(R.id.textView13);
            TextView age = findViewById(R.id.textView15);
            TextView dob = findViewById(R.id.textView19);
            TextView bp = findViewById(R.id.textView4);
            TextView pulse = findViewById(R.id.textView3);
            TextView temperature = findViewById(R.id.textView7);
            TextView address = findViewById(R.id.textView18);
            TextView steps = findViewById(R.id.textView9);
            JSONObject systolicObj = j.getJSONObject("BPsystolic");
            JSONObject diastolicObj = j.getJSONObject("BPdiastolic");
            JSONObject pulseObj = j.getJSONObject("PulseRate");
            JSONObject tempObj = j.getJSONObject("Temperature");
            JSONObject stepsObj = j.getJSONObject("Steps");
            name.setText(j.getString("Name"));
            age.setText(j.getString("Age") + " years");
            dob.setText(j.getString("DOB"));
            bp.setText(systolicObj.getString("Value") + "/" + diastolicObj.getString("Value") + " mm Hg");
            pulse.setText(pulseObj.getString("Value") + " bpm");
            temperature.setText(tempObj.getString("Value") + " deg F");
            steps.setText(stepsObj.getString("Value") + " steps");
            address.setText(j.getString("Address"));
            TextView bpTimestamp = findViewById(R.id.textView2);
            TextView pulseTimestamp = findViewById(R.id.textView6);
            TextView tempTimestamp = findViewById(R.id.textView8);
            TextView stepsTimestamp = findViewById(R.id.textView10);
            //System.out.println(diastolicObj);
            bpTimestamp.setText(diastolicObj.getString("Timestamp"));
            pulseTimestamp.setText(pulseObj.getString("Timestamp"));
            tempTimestamp.setText(tempObj.getString("Timestamp"));
            stepsTimestamp.setText(stepsObj.getString("Timestamp"));
        } catch (JSONException e) {
            System.out.println("OK");
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference dataRef = db.getReference(firebaseAuth.getCurrentUser().getUid());
        ProgressDialog nDialog;
        nDialog = new ProgressDialog(DashboardActivity.this);
        nDialog.setMessage("Loading..");
        nDialog.setTitle("Welcome");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(true);
        nDialog.show();
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                render(snapshot, true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dataRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                render(task.getResult(), false);
                nDialog.dismiss();
            }
        });
        setContentView(R.layout.activity_dashboard);
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, MedicinesActivity.class));
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, TicketDisplayActivity.class));
            }
        });
    }
}