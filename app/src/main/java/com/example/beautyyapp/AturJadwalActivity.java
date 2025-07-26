package com.example.beautyyapp;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AturJadwalActivity extends AppCompatActivity {

    TextView tvNama, tvJenisKulit, tvTujuan;
    EditText etJumlahHari, etJamPagi, etJamMalam, etCatatan;
    Button btnSimpan;

    FirebaseAuth mAuth;
    DatabaseReference userRef, jadwalRef;

    private static final int PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atur_jadwal);

        tvNama = findViewById(R.id.tvNama);
        tvJenisKulit = findViewById(R.id.tvJenisKulit);
        tvTujuan = findViewById(R.id.tvTujuan);
        etJumlahHari = findViewById(R.id.etJumlahHari);
        etJamPagi = findViewById(R.id.etJamPagi);
        etJamMalam = findViewById(R.id.etJamMalam);
        etCatatan = findViewById(R.id.etCatatan);
        btnSimpan = findViewById(R.id.btnSimpan);

        mAuth = FirebaseAuth.getInstance();
        requestNotificationPermission();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            userRef = FirebaseDatabase.getInstance("https://beautyyapp-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("Users").child(uid);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    tvNama.setText(snapshot.child("nama").getValue(String.class));
                    tvJenisKulit.setText(snapshot.child("jenis_kulit").getValue(String.class));
                    tvTujuan.setText(snapshot.child("tujuan_perawatan").getValue(String.class));
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(AturJadwalActivity.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        etJamPagi.setOnClickListener(v -> showTimePicker(etJamPagi));
        etJamMalam.setOnClickListener(v -> showTimePicker(etJamMalam));
        btnSimpan.setOnClickListener(v -> simpanJadwal());
    }

    private void showTimePicker(EditText target) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            String jam = String.format("%02d:%02d", hourOfDay, minute1);
            target.setText(jam);
            target.setTag(new int[]{hourOfDay, minute1}); // Simpan nilai asli
        }, hour, minute, true).show();
    }

    private void simpanJadwal() {
        String jumlahHariStr = etJumlahHari.getText().toString().trim();
        String catatan = etCatatan.getText().toString().trim();

        if (TextUtils.isEmpty(jumlahHariStr) || etJamPagi.getTag() == null || etJamMalam.getTag() == null) {
            Toast.makeText(this, "Isi jumlah hari dan pilih jam dengan benar", Toast.LENGTH_SHORT).show();
            return;
        }

        int jumlahHari = Integer.parseInt(jumlahHariStr);
        int[] pagi = (int[]) etJamPagi.getTag();
        int[] malam = (int[]) etJamMalam.getTag();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            jadwalRef = FirebaseDatabase.getInstance("https://beautyyapp-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("Jadwal").child(uid);

            String key = jadwalRef.push().getKey();

            Map<String, Object> data = new HashMap<>();
            data.put("jumlah_hari", jumlahHari);
            data.put("jam_pagi", String.format("%02d:%02d", pagi[0], pagi[1]));
            data.put("jam_malam", String.format("%02d:%02d", malam[0], malam[1]));
            data.put("catatan", catatan);
            data.put("tanggal_mulai", System.currentTimeMillis());

            jadwalRef.child(key).setValue(data).addOnSuccessListener(unused -> {
                for (int i = 0; i < jumlahHari; i++) {
                    setAlarm("pagi", pagi[0], pagi[1], i);
                    setAlarm("malam", malam[0], malam[1], i);
                }
                Toast.makeText(this, "Jadwal disimpan dan alarm diatur", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Gagal menyimpan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void setAlarm(String waktu, int hour, int minute, int hariOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, hariOffset);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("waktu", waktu);

        int requestCode = (waktu.equals("pagi") ? 1000 : 2000) + hariOffset;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        } else {
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            }
        }
    }
}
