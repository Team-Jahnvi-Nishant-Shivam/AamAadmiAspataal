package com.nsh.covid19.hospital.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nsh.covid19.hospital.R;
import com.nsh.covid19.hospital.activity.OldPatientActivity;
import com.nsh.covid19.hospital.activity.PatientActivity;
import com.nsh.covid19.hospital.model.Patient;
import com.nsh.covid19.hospital.model.Patient;

import java.util.List;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.ViewHolder> {

    List<Patient> patients;
    Context context;
    int type;

    public PatientAdapter(List<Patient> patients, Context context, int type) {
        this.patients = patients;
        this.context = context;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_patient, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Patient appointment = patients.get(position);
        holder.name.setText(appointment.getPatient_name());
        holder.root.setOnClickListener(v -> {
            Intent intent = new Intent(context, OldPatientActivity.class);
            intent.putExtra("patient_name", appointment.getPatient_name());
            intent.putExtra("phone", appointment.getPhone());
            intent.putExtra("patient_id", appointment.getPatient_id());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        RelativeLayout root;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            name = itemView.findViewById(R.id.title1);
        }
    }
}
