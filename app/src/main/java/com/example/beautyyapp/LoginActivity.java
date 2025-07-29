package com.example.beautyyapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button   btnLogin;
    private TextView tvGoToRegister;        // ➜ teks “Belum punya akun?”
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;  // ➜ indikator loading

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ─── Inisialisasi view ────────────────────────────────────────────────
        etEmail        = findViewById(R.id.etLoginEmail);
        etPassword     = findViewById(R.id.etLoginPassword);
        btnLogin       = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister); // pastikan id ada di XML

        // ─── Firebase ────────────────────────────────────────────────────────
        mAuth = FirebaseAuth.getInstance();

        // ─── Progress dialog ────────────────────────────────────────────────
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memproses ...");
        progressDialog.setCancelable(false);

        // ─── Aksi tombol login ──────────────────────────────────────────────
        btnLogin.setOnClickListener(v -> doLogin());

        // ─── Pindah ke halaman register ─────────────────────────────────────
        tvGoToRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    private void doLogin() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Email dan password wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show(); // tampilkan loading

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss(); // sembunyikan loading

                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish(); // supaya tidak bisa kembali ke login
                    } else {
                        String msg = task.getException() != null
                                ? task.getException().getMessage()
                                : "Unknown error";
                        Toast.makeText(this, "Login gagal: " + msg, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
