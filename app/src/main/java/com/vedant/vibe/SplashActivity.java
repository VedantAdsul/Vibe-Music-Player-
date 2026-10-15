package com.vedant.vibe;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    LinearLayout linearLayoutsplash;
    TextView slogan_splash;
    ImageView IVsplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        linearLayoutsplash = findViewById(R.id.linearlayoutsplash);
        slogan_splash = findViewById(R.id.slogan_splash);
        IVsplash = findViewById(R.id.IVsplash);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation textAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        textAnim.setDuration(2000);
        slogan_splash.startAnimation(anim);
        IVsplash.startAnimation(anim);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
    }
