package com.nsh.covid19.hospital.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nsh.covid19.hospital.R;
import com.nsh.covid19.hospital.activity.UploadActivity;
import com.nsh.covid19.hospital.model.Upload;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.ViewHolder> {

    List<Upload> uploads;
    Context context;
    int type;

    public UploadAdapter(List<Upload> uploads, Context context, int type) {
        this.uploads = uploads;
        this.context = context;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_upload, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Upload upload = uploads.get(position);
        Picasso.get().load(upload.getImage()).placeholder(R.drawable.logo).into(holder.image);
        if (type == 0) {
            holder.image.setOnClickListener(v -> {
                Intent intent = new Intent(context, UploadActivity.class);
                intent.putExtra("image", upload.getImage());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }
}
