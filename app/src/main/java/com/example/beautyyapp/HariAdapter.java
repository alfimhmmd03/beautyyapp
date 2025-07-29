package com.example.beautyyapp;

import android.content.Context;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.List;

public class HariAdapter extends RecyclerView.Adapter<HariAdapter.ViewHolder> {

    private final List<String> hariList;
    private int hariSekarang = 1;
    private int jumlahHari = 0;
    private final OnHariClickListener listener;

    public interface OnHariClickListener {
        void onHariClick(int index);
    }

    public HariAdapter(List<String> hariList, OnHariClickListener listener) {
        this.hariList = hariList;
        this.listener = listener;
    }

    public void setHariSekarang(int hariSekarang) {
        this.hariSekarang = hariSekarang;
    }

    public void setJumlahHari(int jumlahHari) {
        this.jumlahHari = jumlahHari;
    }

    @NonNull
    @Override
    public HariAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hari, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HariAdapter.ViewHolder holder, int position) {
        String hari = hariList.get(position);
        int hariKe = position + 1;

        holder.tvHari.setText(hari);
        holder.imgStatus.setImageResource(R.drawable.loading); // default loading

        boolean aktif = (hariKe == hariSekarang);
        boolean lewat = (hariKe < hariSekarang);

        if (aktif) {
            holder.itemView.setEnabled(true);
            holder.tvHari.setTextColor(Color.BLACK);
            holder.itemView.setAlpha(1f);
        } else {
            holder.itemView.setEnabled(false);
            holder.tvHari.setTextColor(Color.GRAY);
            holder.itemView.setAlpha(0.5f);
        }

        // Klik hanya jika aktif
        holder.itemView.setOnClickListener(v -> {
            if (aktif) listener.onHariClick(position);
        });

        // Cek progress Firebase
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String namaHari = "Hari " + hariKe;
        DatabaseReference ref = FirebaseDatabase.getInstance("https://beautyyapp-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Progress").child(uid).child(namaHari);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean pagi = snapshot.child("pagi").getValue(Boolean.class) != null
                        && snapshot.child("pagi").getValue(Boolean.class);
                boolean malam = snapshot.child("malam").getValue(Boolean.class) != null
                        && snapshot.child("malam").getValue(Boolean.class);

                if (pagi && malam) {
                    holder.imgStatus.setImageResource(R.drawable.ic_check_green); // ✅
                } else if (!pagi && !malam && lewat) {
                    holder.imgStatus.setImageResource(R.drawable.ic_close_red); // ❌
                } else {
                    holder.imgStatus.setImageResource(R.drawable.ic_half_check); // ☑️ hanya satu selesai
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.imgStatus.setImageResource(R.drawable.ic_close_red);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hariList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHari;
        ImageView imgStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            tvHari = itemView.findViewById(R.id.tvHari);
            imgStatus = itemView.findViewById(R.id.imgStatus);
        }
    }
}
