package com.example.beautyyapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class DetailHariActivity extends AppCompatActivity {

    TextView tvHari, tvJudul, tvDeskripsi;
    Button btnMulai;
    ImageView imgLangkah;

    String[] langkahs = {
            "Cuci Muka", "Toner", "Serum", "Masker", "Moisturizer", "Sunscreen"
    };

    int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_hari);

        tvHari = findViewById(R.id.tvHari);
        tvJudul = findViewById(R.id.tvJudulLangkah);
        tvDeskripsi = findViewById(R.id.tvDeskripsi);
        btnMulai = findViewById(R.id.btnMulai);
        imgLangkah = findViewById(R.id.imgLangkah);

        int hariKe = getIntent().getIntExtra("hari_ke", 1);
        tvHari.setText("Hari " + hariKe);

        tampilkanLangkah();

        btnMulai.setOnClickListener(v -> {
            Intent intent = new Intent(DetailHariActivity.this, TimerLangkahActivity.class);
            intent.putExtra("langkah", langkahs[currentIndex]);
            startActivity(intent);
        });
    }

    private void tampilkanLangkah() {
        String langkah = langkahs[currentIndex];
        tvJudul.setText(langkah);
        tvDeskripsi.setText("Ini adalah langkah: " + langkah);


        switch (langkah) {
            case "Cuci Muka":
                imgLangkah.setImageResource(R.drawable.cucimukaaaaaa); break;
            case "Toner":
                imgLangkah.setImageResource(R.drawable.tonerrrrrrrrr); break;
            case "Serum":
                imgLangkah.setImageResource(R.drawable.serummmmm); break;
            case "Masker":
                imgLangkah.setImageResource(R.drawable.maskerann); break;
            case "Moisturizer":
                imgLangkah.setImageResource(R.drawable.moistttttttt); break;
            case "Sunscreen":
                imgLangkah.setImageResource(R.drawable.sunscreennnnn); break;
            default:
                imgLangkah.setImageResource(R.drawable.kosmetikpngg);
        }
    }
}
