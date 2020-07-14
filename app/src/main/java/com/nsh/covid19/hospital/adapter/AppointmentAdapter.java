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
import com.nsh.covid19.hospital.activity.LobbyActivity;
import com.nsh.covid19.hospital.activity.PatientActivity;
import com.nsh.covid19.hospital.model.Appointment;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    List<Appointment> appointments;
    Context context;
    int type;

    public AppointmentAdapter(List<Appointment> appointments, Context context, int type) {
        this.appointments = appointments;
        this.context = context;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_appointment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        holder.time.setText(appointment.getTime());
        if (type == 0)
            holder.name.setText(appointment.getPatient_name());
        else
            holder.name.setText(appointment.getDoctor_name());
        holder.root.setOnClickListener(v -> {
            Intent intent = new Intent(context, LobbyActivity.class);
            intent.putExtra("doctor_name", appointment.getDoctor_name());
            intent.putExtra("id", appointment.getappointment_id());
            intent.putExtra("phone", appointment.getPhone());
            intent.putExtra("patient_name", appointment.getPatient_name());
            intent.putExtra("doctor_id", appointment.getDoctor_id());
            intent.putExtra("patient_id", appointment.getPatient_id());
            intent.putExtra("time", appointment.getTime());
            intent.putExtra("message", appointment.getMessage());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView time, name;
        RelativeLayout root;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.title);
            root = itemView.findViewById(R.id.root);
            name = itemView.findViewById(R.id.title1);
        }
    }
}
