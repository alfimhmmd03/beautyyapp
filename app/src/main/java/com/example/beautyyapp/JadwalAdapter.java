package com.example.beautyyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class JadwalAdapter extends RecyclerView.Adapter<JadwalAdapter.ViewHolder> {

    private final List<JadwalModel> list;

    public JadwalAdapter(List<JadwalModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public JadwalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jadwal, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull JadwalAdapter.ViewHolder holder, int position) {
        JadwalModel model = list.get(position);
        holder.tvJam.setText("Jam: " + model.jam_pagi + " & " + model.jam_malam);
        holder.tvCatatan.setText("Catatan: " + (model.catatan == null || model.catatan.isEmpty() ? "-" : model.catatan));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJam, tvCatatan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvJam = itemView.findViewById(R.id.tvJam);
            tvCatatan = itemView.findViewById(R.id.tvCatatan);
        }
    }
}
