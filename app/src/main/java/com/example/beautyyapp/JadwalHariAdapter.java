package com.example.beautyyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class JadwalHariAdapter extends RecyclerView.Adapter<JadwalHariAdapter.ViewHolder> {

    private final List<JadwalModel> list;

    public JadwalHariAdapter(List<JadwalModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hari, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JadwalModel model = list.get(position);
        holder.tvHari.setText(model.hari);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHari;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHari = itemView.findViewById(R.id.tvHari);
        }
    }
}
