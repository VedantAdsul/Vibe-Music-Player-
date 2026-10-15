package com.vedant.vibe.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.AnimationTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.vedant.vibe.MainActivity;
import com.vedant.vibe.R;
import com.vedant.vibe.Model.Song;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private View view;
    private GridLayout gridLayout;
    private ImageSlider slider;
    private SearchView searchView;
    private ImageView mic;
    private TextView afterSearch;
    private TextToSpeech textToSpeech;

    public static List<Song> songList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);
        gridLayout = view.findViewById(R.id.gridlayout);
        searchView = view.findViewById(R.id.searchView);
        mic = view.findViewById(R.id.mic);
        afterSearch = view.findViewById(R.id.aftersearch);

        if (songList.isEmpty()) {
            initializeSongList();
        }

        slider();
        setupLayout();
        setupSearchView();
        speakText();

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden && isResumed())
        {
            if (gridLayout != null) {
                setupLayout();
            }
        }
    }


    private void initializeSongList() {
        songList.add(new Song("0", "Blue", "Young Kai", "path_to_song_0", String.valueOf(R.drawable.blue_youngkai), false));
        songList.add(new Song("1", "Those Eyes", "New West", "path_to_song_1", String.valueOf(R.drawable.those_eyes), false));
        songList.add(new Song("2", "Die With A Smile", "POV", "path_to_song_2", String.valueOf(R.drawable.die_with_a_smile), false));
        songList.add(new Song("3", "CO2", "Chase Atlantic", "path_to_song_3", String.valueOf(R.drawable.co2), false));
        songList.add(new Song("4", "Let Her Go", "Passenger", "path_to_song_4", String.valueOf(R.drawable.let_her_go), false));
        songList.add(new Song("5", "End of Beginning", "Djo", "path_to_song_5", String.valueOf(R.drawable.end_of_beginning), false));
        songList.add(new Song("6", "Wild Side", "ALI ft. AKLO", "path_to_song_6", String.valueOf(R.drawable.wild_side), false));
        songList.add(new Song("7", "Counting Stars", "OneRepublic", "path_to_song_7", String.valueOf(R.drawable.counting_stars), false));
    }


    private void slider() {
        slider = view.findViewById(R.id.imageSlider);
        ArrayList<SlideModel> slideList = new ArrayList<>();

        for (Song song : songList) {
            slideList.add(new SlideModel(Integer.parseInt(song.getAlbumArtUri()), song.getTitle(), ScaleTypes.CENTER_CROP));
        }

        slider.setImageList(slideList, ScaleTypes.CENTER_CROP);
        slider.setSlideAnimation(AnimationTypes.DEPTH_SLIDE);
        slider.startSliding(1000);
        slider.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemSelected(int position) {
                Bundle b = new Bundle();
                b.putInt("song_index", position);
                ((MainActivity) getActivity()).openMusicPlayer(b);
            }
            @Override public void doubleClick(int position) { }
        });
    }

    private void setupLayout() {
        if (getActivity() == null) return;
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        gridLayout.removeAllViews();

        for (int i = 0; i < songList.size(); i++) {
            final Song currentSong = songList.get(i);
            final int songIndex = i;

            View layoutView = layoutInflater.inflate(R.layout.song_card, gridLayout, false);
            ImageView cover = layoutView.findViewById(R.id.coverpage);
            TextView title = layoutView.findViewById(R.id.songname);
            TextView artist = layoutView.findViewById(R.id.artistname);
            ImageView likeButton = layoutView.findViewById(R.id.likebutton);

            cover.setImageResource(Integer.parseInt(currentSong.getAlbumArtUri()));
            title.setText(currentSong.getTitle());
            artist.setText(currentSong.getArtist());
            if (currentSong.isFavourite()) {
                likeButton.setImageResource(R.drawable.ic_heart_liked);
            } else {
                likeButton.setImageResource(R.drawable.ic_heart_unliked);
            }

            layoutView.setOnClickListener(v -> {
                Bundle b = new Bundle();
                b.putInt("song_index", songIndex);
                ((MainActivity) getActivity()).openMusicPlayer(b);
            });

            likeButton.setOnClickListener(v -> {
                currentSong.setFavourite(!currentSong.isFavourite());

                if (currentSong.isFavourite()) {
                    ((ImageView) v).setImageResource(R.drawable.ic_heart_liked);
                    Toast.makeText(getContext(), currentSong.getTitle() + " added to Favourites", Toast.LENGTH_SHORT).show();
                } else {
                    ((ImageView) v).setImageResource(R.drawable.ic_heart_unliked);
                }
            });

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            layoutView.setLayoutParams(params);

            gridLayout.addView(layoutView);
        }
    }

    private void filterGrid(String searchText) {
        if (getActivity() == null) return;
        String query = searchText.toLowerCase(Locale.getDefault());
        gridLayout.removeAllViews();
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

        for (int i = 0; i < songList.size(); i++) {
            Song song = songList.get(i);
            String searchableData = song.getTitle() + " " + song.getArtist();

            if (searchableData.toLowerCase().contains(query)) {
                View layoutView = layoutInflater.inflate(R.layout.song_card, gridLayout, false);

                ImageView cover = layoutView.findViewById(R.id.coverpage);
                TextView title = layoutView.findViewById(R.id.songname);
                TextView artist = layoutView.findViewById(R.id.artistname);
                ImageView likeButton = layoutView.findViewById(R.id.likebutton);

                cover.setImageResource(Integer.parseInt(song.getAlbumArtUri()));
                title.setText(song.getTitle());
                artist.setText(song.getArtist());

                likeButton.setImageResource(song.isFavourite() ? R.drawable.ic_heart_liked : R.drawable.ic_heart_unliked);

                final int index = i;
                likeButton.setOnClickListener(v -> {
                    song.setFavourite(!song.isFavourite());
                    ((ImageView) v).setImageResource(song.isFavourite() ? R.drawable.ic_heart_liked : R.drawable.ic_heart_unliked);
                });

                layoutView.setOnClickListener(v -> {
                    Bundle b = new Bundle();
                    b.putInt("song_index", index);
                    ((MainActivity) getActivity()).openMusicPlayer(b);
                });

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                layoutView.setLayoutParams(params);

                gridLayout.addView(layoutView);
            }
        }
    }

    private void setupSearchView() {
        if (searchView == null) return;
        searchView.setOnClickListener(v -> searchView.setIconified(false));
        EditText editText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (editText != null) {
            editText.setHintTextColor(Color.parseColor("#5E5E5E"));
            editText.setTextColor(Color.BLACK);
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.trim().isEmpty()) {
                    afterSearch.setText("Search Results");
                    filterGrid(query);
                } else {
                    afterSearch.setText("");
                    filterGrid("");
                }
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnCloseListener(() -> {
            afterSearch.setText("Recently Played");
            filterGrid("");
            return false;
        });
    }

    public void speakText() {
        mic.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");
            try {
                startActivityForResult(intent, 1);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == getActivity().RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && !result.isEmpty()) {
                    String spokenText = result.get(0);
                    searchView.setQuery(spokenText, true);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (gridLayout != null) {
            setupLayout();
        }
    }

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

}