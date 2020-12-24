package com.nsh.covid19.hospital.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.nsh.covid19.hospital.R;
import com.nsh.covid19.hospital.adapter.PatientAdapter;
import com.nsh.covid19.hospital.model.Appointment;
import com.nsh.covid19.hospital.model.Patient;
import com.nsh.covid19.hospital.model.Prescription;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ViewOldPatientActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    PatientAdapter patientAdapter;
    List<Patient> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_old_patient);
        SharedPreferences sharedPreferences = getSharedPreferences("covid", 0);

        recyclerView = findViewById(R.id.recyclerView);
        patientAdapter = new PatientAdapter(list, this, sharedPreferences.getInt("type", 1));
        recyclerView.setAdapter(patientAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getData();
    }

    public void getData() {
        findViewById(R.id.pb).setVisibility(View.VISIBLE);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(getSharedPreferences("covid", 0).getString("server","https://883aad4af71a.ngrok.io") + "/doctor/get-all-appointments")
                .method("GET", null)
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.pb).setVisibility(View.GONE);

                            try {
                                JSONArray json1 = json.getJSONArray("all_appointments");
                                for (int i = 0; i < json1.length(); i++) {
                                    list.add(new Patient(json1.getJSONObject(i).getString("time"), FirebaseAuth.getInstance().getCurrentUser().getUid(), json1.getJSONObject(i).getString("patient_id"), json1.getJSONObject(i).getString("problem_description"), json1.getJSONObject(i).getString("name"), "Doctor Name", json1.getJSONObject(i).getString("phone_no")));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            patientAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
