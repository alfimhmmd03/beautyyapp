package com.example.beautyyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    RecyclerView rvHari;
    List<String> listHari;
    HariAdapter hariAdapter;

    int hariSekarang = 1;

    public HomeFragment() {
        // Konstruktor kosong
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ImageView imgMotivasi = view.findViewById(R.id.imgMotivasi);
        Button btnAturJadwal = view.findViewById(R.id.btnAturJadwal);
        rvHari = view.findViewById(R.id.rvHari);

        // Aksi tombol Atur Jadwal
        btnAturJadwal.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AturJadwalActivity.class));
        });

        // Setup RecyclerView
        rvHari.setLayoutManager(new LinearLayoutManager(getContext()));
        listHari = new ArrayList<>();

        hariAdapter = new HariAdapter(listHari, hariIndex -> {
            // Aksi klik hari aktif
            Intent i = new Intent(getContext(), DetailHariActivity.class);
            i.putExtra("hari_ke", hariIndex + 1);
            startActivity(i);
        });

        rvHari.setAdapter(hariAdapter);

        // Ambil data jadwal user (jumlah_hari & tanggal_mulai)
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference jadwalRef = FirebaseDatabase.getInstance("https://beautyyapp-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Jadwal").child(uid);

        jadwalRef.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listHari.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Long jumlahHari = data.child("jumlah_hari").getValue(Long.class);
                    Long tanggalMulai = data.child("tanggal_mulai").getValue(Long.class);

                    if (jumlahHari != null && tanggalMulai != null) {
                        // Hitung hari sekarang
                        long now = System.currentTimeMillis();
                        long selisihHari = TimeUnit.MILLISECONDS.toDays(now - tanggalMulai);
                        hariSekarang = (int) selisihHari + 1;

                        for (int i = 1; i <= jumlahHari; i++) {
                            listHari.add("Hari " + i);
                        }

                        hariAdapter.setHariSekarang(hariSekarang);
                        hariAdapter.setJumlahHari(jumlahHari.intValue());
                        hariAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Gagal memuat data jadwal", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
