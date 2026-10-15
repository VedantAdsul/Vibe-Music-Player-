package com.vedant.vibe;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        VideoView videoView = findViewById(R.id.videoView);
        Button stretchButton = findViewById(R.id.stretchButton);
        Button shrinkButton = findViewById(R.id.shrinkButton);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.never_gonna_give_you_up);
        videoView.start();

        stretchButton.setOnClickListener(v -> {
            videoView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            videoView.requestLayout();
        });

        shrinkButton.setOnClickListener(v -> {
            videoView.getLayoutParams().height = 200;
            videoView.requestLayout();
        });
    }
}