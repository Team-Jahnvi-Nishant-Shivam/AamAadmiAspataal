package com.nsh.covid19.hospital.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.nsh.covid19.hospital.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.option).setOnClickListener(this);
        findViewById(R.id.option1).setOnClickListener(this);
        findViewById(R.id.option2).setOnClickListener(this);
        findViewById(R.id.profile).setOnClickListener(this);
        findViewById(R.id.option3).setOnClickListener(this);
        findViewById(R.id.option4).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
            case R.id.option:
                startActivity(new Intent(MainActivity.this, ViewAppointmentActivity.class));
                break;
            case R.id.option1:
                startActivity(new Intent(MainActivity.this, CreateAppointmentActivity.class));
                break;
            case R.id.option2:
                startActivity(new Intent(MainActivity.this, OldPrescriptionActivity.class));
                break;
            case R.id.option3:
                startActivity(new Intent(MainActivity.this, LiveQuestionActivity.class));
                break;
            case R.id.option4:
                SharedPreferences sharedPreferences = getSharedPreferences("covid",0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, IntroActivity.class));
                finish();
                break;
        }
    }
}
