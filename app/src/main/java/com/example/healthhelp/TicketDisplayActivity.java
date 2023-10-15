package com.example.healthhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class TicketDisplayActivity extends AppCompatActivity {
    private void render(QuerySnapshot ds, boolean redone) {
        Gson g = new Gson();
        try {
            JSONObject j = new JSONObject(g.toJson(ds.));
            Iterator<String> keys = j.keys();
            LinearLayout lldd = findViewById(R.id.lldd);
            lldd.removeAllViews();
            System.out.println(j);
            while (keys.hasNext()) {
                String doc_id = keys.next();
                JSONObject docdoc = new JSONObject(g.toJson());
                String docname = docdoc.getString("Name");
                String docspecial = docdoc.getString("Specialization");
                LinearLayout ll = new LinearLayout(this);
                ll.setOrientation(LinearLayout.VERTICAL);
                TextView docName = new TextView(this);
                docName.setTextSize(24);
                docName.setTypeface(null, Typeface.BOLD);
                docName.setText(docname);
                ll.addView(docName);
                lldd.addView(ll);
            }
        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        db.collection("chat").document().collection(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                render(task.getResult(), false);
            }
        });
        setContentView(R.layout.activity_ticket_display);
    }
}