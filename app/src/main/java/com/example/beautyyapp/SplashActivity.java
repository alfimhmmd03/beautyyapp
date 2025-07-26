package com.example.beautyyapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beautyyapp.LoginActivity;
import com.example.beautyyapp.R;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME = 4000; // 4 detik
    ImageView logoSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logoSplash = findViewById(R.id.logoSplash);

        // Load animasi
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        logoSplash.startAnimation(animation);

        // Delay sebelum pindah ke login atau main
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_TIME);
    }
}
