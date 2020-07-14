package com.nsh.covid19.hospital.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.nsh.covid19.hospital.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText name, email, license;
    private Spinner spinner;
    String[] spe = {"Select a Specialization", "Allergist", "Cardiologist", "Audiologist", "Dentist", "Dermatologist", "Endocrinologist", "Epidemiologist", "Anesthesiologist", "Gynaecologist", "Geneticist", "Microbiologist", "Neonatologist", "Neurologist", "Neurosurgeon", "ENT Specialist", "Paediatrician", "Physiologist"};
    String spefinal = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        license = findViewById(R.id.license);
        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter aa = new ArrayAdapter(this, R.layout.simple_spinner_item, spe);
        aa.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);

        findViewById(R.id.btn_start).setOnClickListener(v -> save());

        try {
            getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getData() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://fa4b4c235834.ngrok.io/doctor/get-details")
                .method("POST", body)
                .addHeader("Authorization", "doctor " + FirebaseAuth.getInstance().getCurrentUser().getUid())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    JSONObject json1 = json.getJSONObject("doctor");
                    runOnUiThread(() -> {
                        try {
                            System.out.println(json1);
                            name.setText(json1.getString("name"));
                            email.setText(json1.getString("email_id"));
                            license.setText(json1.getString("registration_no"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void save() {

        if (spefinal.equals(""))
            Toast.makeText(this, "Please select a specialization", Toast.LENGTH_SHORT).show();
        else {
            findViewById(R.id.pb).setVisibility(View.VISIBLE);

            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("doctors")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("name")
                    .setValue(String.valueOf(name.getText()));
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("doctors")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("email")
                    .setValue(String.valueOf(email.getText()));
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("doctors")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("specialization")
                    .setValue(spefinal);
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("doctors")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("phone")
                    .setValue(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("specialization")
                    .child(spefinal)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(String.valueOf(name.getText()));
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("https://fa4b4c235834.ngrok.io/doctor/update-details?name=" + String.valueOf(name.getText()) + "&email=" + String.valueOf(email.getText()) + "&specialization=" + spefinal + "&registration_no=" + String.valueOf(license.getText()) + "&email_id=" + String.valueOf(email.getText()))
                    .method("POST", body)
                    .addHeader("Authorization", "doctor " + FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(() -> {
                        findViewById(R.id.pb).setVisibility(View.GONE);

                        Toast.makeText(ProfileActivity.this, "Profile Saved.", Toast.LENGTH_LONG).show();
                    });
                }
            });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) spefinal = "";
        else spefinal = spe[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }
}

