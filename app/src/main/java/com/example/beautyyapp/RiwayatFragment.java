package com.example.beautyyapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class RiwayatFragment extends Fragment {

    RecyclerView recyclerView;
    JadwalAdapter adapter;
    List<JadwalModel> jadwalList;
    DatabaseReference jadwalRef;

    public RiwayatFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_riwayat, container, false);

        recyclerView = view.findViewById(R.id.rvJadwal);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        jadwalList = new ArrayList<>();
        adapter = new JadwalAdapter(jadwalList);
        recyclerView.setAdapter(adapter);

        // Ambil UID user
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        jadwalRef = FirebaseDatabase.getInstance("https://beautyyapp-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Jadwal").child(uid);

        jadwalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jadwalList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    JadwalModel model = data.getValue(JadwalModel.class);

// Generate hari dari timestamp kalau 'hari' null
                    if (model.getHari() == null && model.getTanggalMulai() != 0) {
                        String hari = new java.text.SimpleDateFormat("EEEE", java.util.Locale.getDefault())
                                .format(new java.util.Date(model.getTanggalMulai()));
                        model.setHari(hari);
                    }

                    jadwalList.add(model);

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}