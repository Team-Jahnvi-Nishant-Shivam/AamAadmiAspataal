package com.nsh.covid19.hospital.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nsh.covid19.hospital.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateAppointmentActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText name, phone, message;
    private Spinner spinner, doctor;
    String[] spe = {"Select a Specialization", "Allergist", "Cardiologist", "Audiologist", "Dentist", "Dermatologist", "Endocrinologist", "Epidemiologist", "Anesthesiologist", "Gynaecologist", "Geneticist", "Microbiologist", "Neonatologist", "Neurologist", "Neurosurgeon", "ENT Specialist", "Paediatrician", "Physiologist"};
    ArrayList<String> doc = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    String spefinal = "";
    String doctorfinal = "";
    String doctorfinalid = "";
    ArrayAdapter<String> aa, aa1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appointment);

        doc.add("No Doctors");
        ids.add("");

        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        message = findViewById(R.id.message);

        phone.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        aa = new ArrayAdapter<>(this, R.layout.simple_spinner_item, spe);
        aa.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);

        doctor = findViewById(R.id.doctor);
        doctor.setOnItemSelectedListener(this);

        aa1 = new ArrayAdapter<>(this, R.layout.simple_spinner_item, doc);
        aa1.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        doctor.setAdapter(aa1);

        findViewById(R.id.btn_start).setOnClickListener(v -> {
            try {
                createAppointment();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void createAppointment() throws IOException {
        findViewById(R.id.pb).setVisibility(View.VISIBLE);
        System.out.println(FirebaseAuth.getInstance().getCurrentUser().getUid());
        System.out.println(String.valueOf(name.getText()));
        System.out.println(String.valueOf(phone.getText()));
        System.out.println(doctorfinal);
        System.out.println(String.valueOf(message.getText()));

        SharedPreferences sharedPreferences = getSharedPreferences("covid", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", String.valueOf(name.getText()));

        OkHttpClient client1 = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType1 = MediaType.parse("text/plain");
        RequestBody body1 = RequestBody.create(mediaType1, "");
        Request request1 = new Request.Builder()
                .url("https://fa4b4c235834.ngrok.io/patient/update-details?name=" + String.valueOf(name.getText()) + "&phone_no=" + String.valueOf(phone.getText()))
                .method("POST", body1)
                .addHeader("Authorization", "patient " + FirebaseAuth.getInstance().getCurrentUser().getUid())
                .build();
        client1.newCall(request1).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://fa4b4c235834.ngrok.io/patient/book-appointment?doctor_id=" + doctorfinalid + "&problem_description=" + String.valueOf(message.getText()))
                .method("POST", body)
                .addHeader("Authorization", "patient " + FirebaseAuth.getInstance().getCurrentUser().getUid())
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
                                Toast.makeText(CreateAppointmentActivity.this, "Appointment Booked for " + json.getString("appointment_time"), Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                Toast.makeText(CreateAppointmentActivity.this, "Appointment Already Booked", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinner) {
            doc = new ArrayList<>();
            ids = new ArrayList<>();
            if (position != 0) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference("specialization/" + spe[position]);

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            doc.add("No Doctors");
                            ids.add("");
                            aa1.clear();
                            aa1.addAll(doc);
                        } else {
                            doc.add("Select a Doctor");
                            ids.add("");
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String name = postSnapshot.getValue(String.class);
                                doc.add(name);
                                ids.add(postSnapshot.getKey());
                            }
                            aa1.clear();
                            aa1.addAll(doc);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
            } else {
                spefinal = "";
                doc.add("No Doctors");
                ids.add("");
                aa1.clear();
                aa1.addAll(doc);
            }
        } else {
            if (position != 0) {
                doctorfinal = doc.get(position);
                doctorfinalid = ids.get(position);
            } else {
                doctorfinal = "";
                doctorfinalid = "";
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }
}
