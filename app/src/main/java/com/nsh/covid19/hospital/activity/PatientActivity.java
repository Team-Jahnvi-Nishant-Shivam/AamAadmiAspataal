package com.nsh.covid19.hospital.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nsh.covid19.hospital.ContactData;
import com.nsh.covid19.hospital.FirebaseData;
import com.nsh.covid19.hospital.R;
import com.nsh.covid19.hospital.adapter.UploadAdapter;
import com.nsh.covid19.hospital.model.Upload;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class PatientActivity extends AppCompatActivity {

    TextView name, phone, message, upload;
    RecyclerView recyclerView;
    UploadAdapter patientAdapter;
    List<Upload> list = new ArrayList<>();
    EditText prescription;

    DatabaseReference callRef;
    ValueEventListener callListener, usersListener;
    String patient_id = "";
    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        startActivity(new Intent(PatientActivity.this, LobbyActivity.class));

        FirebaseDatabase.getInstance().getReference("users/" + id + "/online").onDisconnect().setValue(false);
        FirebaseDatabase.getInstance().getReference("users/" + id).setValue(new ContactData(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), true));

        name = findViewById(R.id.option);
        phone = findViewById(R.id.option1);
        message = findViewById(R.id.option2);
        prescription = findViewById(R.id.message);
        upload = findViewById(R.id.option3);

        name.setText(getIntent().getStringExtra("patient_name"));
        phone.setText(getIntent().getStringExtra("phone"));
        message.setText(getIntent().getStringExtra("message"));

        patient_id = "HhuihCXjRaPVhKU5oXLjTJE3BgZ2";

        SharedPreferences sharedPreferences = getSharedPreferences("covid", 0);
        int type = sharedPreferences.getInt("type", 1);

        if (type == 0) {
            upload.setVisibility(GONE);
        } else {
            prescription.setVisibility(GONE);
            findViewById(R.id.call).setVisibility(GONE);
        }

        recyclerView = findViewById(R.id.recyclerView);
        patientAdapter = new UploadAdapter(list, this, type);
        recyclerView.setAdapter(patientAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        getData();

        findViewById(R.id.call).setOnClickListener(v -> {
            startVideoCall();
        });

        findViewById(R.id.end).setOnClickListener(v -> {
            if (sharedPreferences.getInt("type", 1) == 0)
                startActivity(new Intent(PatientActivity.this, DoctorMainActivity.class));
            else
                startActivity(new Intent(PatientActivity.this, MainActivity.class));
            finish();
        });

        callRef = FirebaseDatabase.getInstance().getReference("calls/" + id + "/id");
        callListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    receiveVideoCall(snapshot.getValue(String.class));
                    callRef.removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        usersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    return;
                }
                for (DataSnapshot ds : snapshot.getChildren()) {
                    System.out.println(ds.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        callRef.addValueEventListener(callListener);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addValueEventListener(usersListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        callRef.removeEventListener(callListener);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addValueEventListener(usersListener);
    }

    public void startVideoCall() {
        FirebaseDatabase.getInstance().getReference("calls/" + id + "/status").setValue(true);
        FirebaseDatabase.getInstance().getReference("calls/" + patient_id + "/id").onDisconnect().removeValue();
        FirebaseDatabase.getInstance().getReference("calls/" + patient_id + "/id").setValue(id);
        VideoCallActivity.Companion.startCall(PatientActivity.this, patient_id);
    }

    public void receiveVideoCall(String key) {
        VideoCallActivity.Companion.receiveCall(this, key);
    }

    public void getData() {
        list.add(new Upload("https://oryon.co.uk/wp-content/uploads/2019/08/Brain-MRI-e1565353833878.jpg"));
        list.add(new Upload("https://media.sciencephoto.com/image/p6800417/800wm/P6800417-Coloured_ultrasound_scan_of_foetus_aged_10_weeks.jpg"));
        list.add(new Upload("https://media.sciencephoto.com/image/p6800417/800wm/P6800417-Coloured_ultrasound_scan_of_foetus_aged_10_weeks.jpg"));
        list.add(new Upload("https://media.sciencephoto.com/image/p6800417/800wm/P6800417-Coloured_ultrasound_scan_of_foetus_aged_10_weeks.jpg"));
        list.add(new Upload("https://media.sciencephoto.com/image/p6800417/800wm/P6800417-Coloured_ultrasound_scan_of_foetus_aged_10_weeks.jpg"));
        patientAdapter.notifyDataSetChanged();
    }
}
