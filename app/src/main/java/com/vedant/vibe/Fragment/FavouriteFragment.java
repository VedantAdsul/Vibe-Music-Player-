package com.vedant.vibe.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.vedant.vibe.Adapter.FavouriteAdapter;
import com.vedant.vibe.MainActivity;
import com.vedant.vibe.Model.Song;
import com.vedant.vibe.databinding.FragmentFavouriteBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FavouriteFragment extends Fragment {

    private FragmentFavouriteBinding binding;
    private FavouriteAdapter adapter;
    public static List<Song> favouriteSongs = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).bottomNavigationView.setVisibility(View.GONE);
        }

        binding.favouriteRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new FavouriteAdapter(favouriteSongs, (song, position) -> {
            for (Song s : HomeFragment.songList) {
                if (s.getId().equals(song.getId())) {
                    s.setFavourite(false);
                    break;
                }
            }

            favouriteSongs.remove(position);
            adapter.notifyItemRemoved(position);

            checkEmptyState();
        });

        binding.favouriteRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavouriteSongs();
    }

    private void loadFavouriteSongs() {
        favouriteSongs.clear();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            favouriteSongs.addAll(
                    HomeFragment.songList.stream()
                            .filter(Song::isFavourite)
                            .collect(Collectors.toList())
            );
        } else {
            for (Song song : HomeFragment.songList) {
                if (song.isFavourite()) {
                    favouriteSongs.add(song);
                }
            }
        }

        checkEmptyState();
        if(adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void checkEmptyState() {
        if (favouriteSongs.isEmpty()) {
            binding.favouriteRecyclerView.setVisibility(View.GONE);
            binding.emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            binding.favouriteRecyclerView.setVisibility(View.VISIBLE);
            binding.emptyStateLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }
}