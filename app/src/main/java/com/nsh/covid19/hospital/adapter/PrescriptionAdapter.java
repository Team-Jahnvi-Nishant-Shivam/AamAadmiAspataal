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
import com.nsh.covid19.hospital.activity.PatientActivity;
import com.nsh.covid19.hospital.model.Prescription;

import java.util.List;

public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.ViewHolder> {

    List<Prescription> prescriptions;
    Context context;
    int type;

    public PrescriptionAdapter(List<Prescription> prescriptions, Context context, int type) {
        this.prescriptions = prescriptions;
        this.context = context;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_prescription, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Prescription Prescription = prescriptions.get(position);
        holder.time.setText(Prescription.getTime());
        holder.name.setText(Prescription.getDoctor_name());
        holder.message.setText(Prescription.getMessage());
    }

    @Override
    public int getItemCount() {
        return prescriptions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView time, name, message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.title);
            message = itemView.findViewById(R.id.title2);
            name = itemView.findViewById(R.id.title1);
        }
    }
}
