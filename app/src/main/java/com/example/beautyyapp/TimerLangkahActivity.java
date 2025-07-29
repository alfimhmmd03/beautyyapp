package com.example.beautyyapp;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class TimerLangkahActivity extends AppCompatActivity {

    TextView tvLangkah, tvCountdown, tvInstruksi;
    ImageView imgLangkah;
    Button btnLewati, btnLanjut;

    int langkahIndex = 0;
    int hariKe = 1;
    CountDownTimer timer;
    List<LangkahModel> langkahList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_langkah);

        tvLangkah = findViewById(R.id.tvLangkah);
        tvCountdown = findViewById(R.id.tvCountdown);
        tvInstruksi = findViewById(R.id.tvInstruksi);
        imgLangkah = findViewById(R.id.imgLangkah);
        btnLewati = findViewById(R.id.btnLewati);
        btnLanjut = findViewById(R.id.btnLanjut);

        btnLanjut.setEnabled(false);

        String waktu = getIntent().getStringExtra("waktu"); // "pagi" atau "malam"
        hariKe = getIntent().getIntExtra("hari_ke", 1); // opsional

        langkahList = getLangkahList(waktu);
        mulaiLangkah();

        btnLewati.setOnClickListener(v -> {
            if (timer != null) timer.cancel();
            lanjutLangkah();
        });

        btnLanjut.setOnClickListener(v -> lanjutLangkah());
    }

    private List<LangkahModel> getLangkahList(String waktu) {
        List<LangkahModel> list = new ArrayList<>();
        list.add(new LangkahModel("Cuci Muka", 30, "Tunggu kulit mengering"));
        list.add(new LangkahModel("Toner", 30, "Tunggu toner menyerap"));
        list.add(new LangkahModel("Serum", 30, "Tunggu serum menyerap"));
        list.add(new LangkahModel("Masker", 900, "Tunggu masker menyerap")); // 15 menit
        list.add(new LangkahModel("Moisturizer", 30, "Tunggu meresap"));

        if ("pagi".equals(waktu)) {
            list.add(new LangkahModel("Sunscreen", 900, "Gunakan sebelum keluar rumah"));
        }

        return list;
    }

    private void mulaiLangkah() {
        if (langkahIndex >= langkahList.size()) {
            simpanProgress();
            return;
        }

        LangkahModel langkah = langkahList.get(langkahIndex);
        tvLangkah.setText("Langkah: " + langkah.nama);
        tvInstruksi.setText(langkah.instruksi);
        imgLangkah.setImageResource(getImageForLangkah(langkah.nama));
        btnLanjut.setEnabled(false);

        timer = new CountDownTimer(langkah.durasi * 1000L, 1000) {
            int berjalan = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                berjalan++;
                tvCountdown.setText(berjalan + "/" + langkah.durasi + " detik");
            }

            @Override
            public void onFinish() {
                tvCountdown.setText("Selesai!");
                btnLanjut.setEnabled(true);
            }
        }.start();
    }

    private void lanjutLangkah() {
        langkahIndex++;
        mulaiLangkah();
    }

    private void simpanProgress() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String namaHari = "Hari " + hariKe;

        FirebaseDatabase.getInstance("https://beautyyapp-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Progress").child(uid).child(namaHari)
                .setValue(true)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Hari selesai! ✔", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal simpan progress", Toast.LENGTH_SHORT).show();
                });
    }

    private int getImageForLangkah(String langkah) {
        switch (langkah) {
            case "Cuci Muka": return R.drawable.cucimukaaaaaa;
            case "Toner": return R.drawable.tonerrrrrrrrr;
            case "Serum": return R.drawable.serummmmm;
            case "Masker": return R.drawable.maskerann;
            case "Moisturizer": return R.drawable.moistttttttt;
            case "Sunscreen": return R.drawable.sunscreennnnn;
            default: return R.drawable.kosmetikpngg;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }

    // Inner class untuk model langkah
    static class LangkahModel {
        String nama;
        int durasi;
        String instruksi;

        LangkahModel(String nama, int durasi, String instruksi) {
            this.nama = nama;
            this.durasi = durasi;
            this.instruksi = instruksi;
        }
    }
}
