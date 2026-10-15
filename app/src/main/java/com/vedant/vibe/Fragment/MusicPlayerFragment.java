package com.vedant.vibe.Fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.vedant.vibe.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MusicPlayerFragment extends Fragment {

    private TextView song_name, song_currentTime, song_totalTime;
    private SeekBar song_progress;
    private ImageView btn_shuffle, btn_previous, btn_play, btn_next, btn_repeat, song_image, icon_forward, icon_backward;
    private ListView listView;
    private View tapLeft, tapRight;
    private View view;

    private MediaPlayer mediaPlayer;
    private int currentSong = 0;
    private boolean isRepeating = false;
    private boolean isShuffling = false;
    private final Handler handler = new Handler();
    private final Random random = new Random();

    private final int[] rawSongs = {
            R.raw.blue_yungkai, R.raw.those_eyes, R.raw.die_with_a_smile, R.raw.co2,
            R.raw.let_her_go, R.raw.end_of_beginning, R.raw.wild_side, R.raw.counting_stars
    };

    private final String[] titles = {
            "Blue", "Those Eyes", "Die With A Smile", "CO2",
            "Let Her Go", "End of Beginning", "Wild Side", "Counting Stars"
    };

    private final int[] images = {
            R.drawable.blue_youngkai, R.drawable.those_eyes, R.drawable.die_with_a_smile, R.drawable.co2,
            R.drawable.let_her_go, R.drawable.end_of_beginning, R.drawable.wild_side, R.drawable.counting_stars
    };

    private final Runnable updateSongProgress = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                int sTime = mediaPlayer.getCurrentPosition();
                song_progress.setProgress(sTime);
                song_currentTime.setText(formatTime(sTime));
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_music_player, container, false);

        findViews();
        setupListView();
        setupEventListeners();

        updateUI(currentSong);

        return view;
    }

    public void setSong(int songIndex) {
        if (currentSong == songIndex && mediaPlayer != null && mediaPlayer.isPlaying()) {
            return;
        }
        Log.d("MusicPlayer", "Switching to song index: " + songIndex);
        playSong(songIndex);
    }

    private void findViews() {
        song_name = view.findViewById(R.id.song_name);
        song_currentTime = view.findViewById(R.id.song_currentTime);
        song_totalTime = view.findViewById(R.id.song_totalTime);
        song_progress = view.findViewById(R.id.song_progress);
        btn_shuffle = view.findViewById(R.id.btn_shuffle);
        btn_previous = view.findViewById(R.id.btn_previous);
        btn_play = view.findViewById(R.id.btn_play);
        btn_next = view.findViewById(R.id.btn_next);
        btn_repeat = view.findViewById(R.id.btn_repeat);
        song_image = view.findViewById(R.id.song_image);
        listView = view.findViewById(R.id.listview);
        tapLeft = view.findViewById(R.id.tap_left);
        tapRight = view.findViewById(R.id.tap_right);
        icon_forward = view.findViewById(R.id.icon_forward);
        icon_backward = view.findViewById(R.id.icon_rewind);
    }

    private void setupListView() {
        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("title", titles[i]);
            map.put("icon", images[i]);
            data.add(map);
        }

        String[] from = {"title", "icon"};
        int[] to = {R.id.item_title, R.id.item_icon};

        SimpleAdapter adapter = new SimpleAdapter(requireContext(), data, R.layout.list_items, from, to) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView title = view.findViewById(R.id.item_title);
                if (position == currentSong) {
                    view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.highlight_bg));
                    title.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selected));
                } else {
                    view.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent));
                    title.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_normal));
                }
                return view;
            }
        };

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> playSong(position));
    }

    private void setupEventListeners() {
        btn_play.setOnClickListener(v -> togglePlayPause());

        btn_next.setOnClickListener(v -> playNextSong(false));

        btn_previous.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.getCurrentPosition() > 3000) {
                playSong(currentSong);
            } else {
                playPreviousSong();
            }
        });

        btn_repeat.setOnClickListener(v -> {
            isRepeating = !isRepeating;
            btn_repeat.setColorFilter(isRepeating ? ContextCompat.getColor(requireContext(), R.color.icon_tint_selected)
                    : ContextCompat.getColor(requireContext(), R.color.black));
        });

        btn_shuffle.setOnClickListener(v -> {
            isShuffling = !isShuffling;
            btn_shuffle.setColorFilter(isShuffling ? ContextCompat.getColor(requireContext(), R.color.icon_tint_selected)
                    : ContextCompat.getColor(requireContext(), R.color.black));
        });

        song_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    song_currentTime.setText(formatTime(progress));
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateSongProgress);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                    if (mediaPlayer.isPlaying()) {
                        handler.post(updateSongProgress);
                    }
                }
            }
        });

        final GestureDetector gestureDetector = new GestureDetector(requireContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) { return true; }
        });

        tapLeft.setOnTouchListener((v, event) -> {
            if (gestureDetector.onTouchEvent(event) && mediaPlayer != null) {
                mediaPlayer.seekTo(Math.max(mediaPlayer.getCurrentPosition() - 5000, 0));
                updateProgressUI();
                animateIcon(icon_backward);
            }
            return true;
        });

        tapRight.setOnTouchListener((v, event) -> {
            if (gestureDetector.onTouchEvent(event) && mediaPlayer != null) {
                mediaPlayer.seekTo(Math.min(mediaPlayer.getCurrentPosition() + 5000, mediaPlayer.getDuration()));
                updateProgressUI();
                animateIcon(icon_forward);
            }
            return true;
        });
    }

    private void playSong(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        currentSong = position;
        mediaPlayer = MediaPlayer.create(requireContext(), rawSongs[position]);

        if (mediaPlayer == null) return;

        updateUI(position);
        mediaPlayer.setOnCompletionListener(mp -> playNextSong(true));
        mediaPlayer.start();

        btn_play.setImageResource(R.drawable.pause_button);
        handler.post(updateSongProgress);
    }

    private void togglePlayPause() {
        if (mediaPlayer == null) {
            playSong(currentSong);
            return;
        }

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            btn_play.setImageResource(R.drawable.play_button);
            handler.removeCallbacks(updateSongProgress);
        } else {
            mediaPlayer.start();
            btn_play.setImageResource(R.drawable.pause_button);
            handler.post(updateSongProgress);
        }
    }

    private void playNextSong(boolean fromCompletion) {
        int nextSong = currentSong;
        if (isRepeating && fromCompletion) {
            playSong(currentSong);
        } else if (isShuffling) {
            nextSong = random.nextInt(rawSongs.length);
            if (nextSong == currentSong) {
                nextSong = (nextSong + 1) % rawSongs.length;
            }
        } else {
            nextSong = (currentSong + 1) % rawSongs.length;
        }
        playSong(nextSong);
    }

    private void playPreviousSong() {
        int prevSong = (currentSong > 0) ? currentSong - 1 : rawSongs.length - 1;
        playSong(prevSong);
    }
    private void updateProgressUI() {
        if (mediaPlayer != null) {
            song_progress.setProgress(mediaPlayer.getCurrentPosition());
            song_currentTime.setText(formatTime(mediaPlayer.getCurrentPosition()));
        }
    }

        private void updateUI(int position) {
        song_name.setText(titles[position]);
        song_image.setImageResource(images[position]);

        if (mediaPlayer != null) {
            int duration = mediaPlayer.getDuration();
            song_progress.setMax(duration);
            song_totalTime.setText(formatTime(duration));
            int currentTime = mediaPlayer.getCurrentPosition();
            song_progress.setProgress(currentTime);
            song_currentTime.setText(formatTime(currentTime));
        } else {
            song_progress.setMax(100);
            song_progress.setProgress(0);
            song_totalTime.setText(formatTime(0));
            song_currentTime.setText(formatTime(0));
        }

        if (listView != null && listView.getAdapter() != null) {
            ((SimpleAdapter) listView.getAdapter()).notifyDataSetChanged();
            listView.smoothScrollToPosition(position);
        }
    }

    private String formatTime(int milliseconds) {
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    private void animateIcon(ImageView icon) {
        icon.setAlpha(1f);
        icon.setVisibility(View.VISIBLE);
        icon.animate().alpha(0f).setDuration(500).start();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            btn_play.setImageResource(R.drawable.play_button);
            handler.removeCallbacks(updateSongProgress);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(updateSongProgress);
    }
}