package com.nsh.covid19.hospital.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nsh.covid19.hospital.R;

public class DoctorMainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_doctor);

        findViewById(R.id.option).setOnClickListener(this);
        findViewById(R.id.option2).setOnClickListener(this);
        findViewById(R.id.profile).setOnClickListener(this);
        findViewById(R.id.option3).setOnClickListener(this);
        findViewById(R.id.option4).setOnClickListener(this);

        DatabaseReference callRef = FirebaseDatabase.getInstance().getReference("server");
        ValueEventListener callListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    SharedPreferences preference = getSharedPreferences("covid", 0);
                    SharedPreferences.Editor editor = preference.edit();
                    editor.putString("server", snapshot.getValue(String.class));
                    editor.commit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        callRef.addValueEventListener(callListener);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile:
                startActivity(new Intent(DoctorMainActivity.this, ProfileActivity.class));
                break;
            case R.id.option:
                startActivity(new Intent(DoctorMainActivity.this, ViewAppointmentActivity.class));
                break;
            case R.id.option2:
                startActivity(new Intent(DoctorMainActivity.this, ViewOldPatientActivity.class));
                break;
            case R.id.option3:
                startActivity(new Intent(DoctorMainActivity.this, LiveQuestionActivity.class));
                break;
            case R.id.option4:
                SharedPreferences sharedPreferences = getSharedPreferences("covid",0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(DoctorMainActivity.this, IntroActivity.class));
                finish();
                break;
        }
    }
}
