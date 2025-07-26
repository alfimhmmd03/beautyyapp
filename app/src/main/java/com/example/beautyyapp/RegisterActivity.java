package com.example.beautyyapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText etNama, etJenisKulit, etTujuan, etEmail, etPassword;
    Button btnRegister;

    FirebaseAuth mAuth;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialisasi komponen UI
        etNama       = findViewById(R.id.etNama);
        etJenisKulit = findViewById(R.id.etJenisKulit);
        etTujuan     = findViewById(R.id.etTujuan);
        etEmail      = findViewById(R.id.etEmail);
        etPassword   = findViewById(R.id.etPassword);
        btnRegister  = findViewById(R.id.btnRegister);

        mAuth = FirebaseAuth.getInstance();

        loading = new ProgressDialog(this);
        loading.setMessage("Mendaftarkan akun...");
        loading.setCancelable(false);

        // Event saat tombol register ditekan
        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String nama      = etNama.getText().toString().trim();
        String jenisKulit = etJenisKulit.getText().toString().trim();
        String tujuan    = etTujuan.getText().toString().trim();
        String email     = etEmail.getText().toString().trim();
        String password  = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(nama) || TextUtils.isEmpty(jenisKulit)
                || TextUtils.isEmpty(tujuan) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        loading.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        String uid = user.getUid();

                        Map<String, Object> data = new HashMap<>();
                        data.put("email", email);
                        data.put("nama", nama);
                        data.put("jenis_kulit", jenisKulit);
                        data.put("tujuan_perawatan", tujuan);

                        FirebaseDatabase.getInstance("https://beautyyapp-default-rtdb.asia-southeast1.firebasedatabase.app")
                                .getReference("Users")
                                .child(uid)
                                .setValue(data)
                                .addOnSuccessListener(unused -> {
                                    loading.dismiss();
                                    Toast.makeText(this, "Registrasi sukses! Silakan login.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, LoginActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    loading.dismiss();
                                    Toast.makeText(this, "Gagal simpan data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    loading.dismiss();
                    Toast.makeText(this, "Gagal registrasi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
