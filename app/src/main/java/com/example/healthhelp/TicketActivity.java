package com.example.healthhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

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

import java.util.Iterator;

public class TicketActivity extends AppCompatActivity {

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
                Space space = new Space(this);
                space.setMinimumHeight(100);
                ll.addView(space);
                ll.addView(medicineName);
                JSONObject times = j.getJSONObject(key);
                Iterator<String> itimes = times.keys();
                while (itimes.hasNext()) {
                    String time = itimes.next();
                    TextView timeOfDay = new TextView(this);
                    timeOfDay.setTextSize(15);
                    timeOfDay.setTypeface(null, Typeface.ITALIC);
                    timeOfDay.setText(time + ": " + times.getString(time));
                    ll.addView(timeOfDay);
                }
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
                b.setBackgroundColor(Color.RED);
                ll.addView(b);
                ltt.addView(ll);
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
        DatabaseReference dataRef = db.getReference("chat").child(firebaseAuth.getCurrentUser().getUid()).child("Medicines");
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
        setContentView(R.layout.activity_ticket);
    }
}