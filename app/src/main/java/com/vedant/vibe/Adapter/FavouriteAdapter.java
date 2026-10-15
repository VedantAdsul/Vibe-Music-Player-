package com.vedant.vibe.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.vedant.vibe.Model.Song;
import com.vedant.vibe.R;

import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder> {

    private List<Song> favouriteSongs;
    private OnUnlikeClickListener listener;

    public interface OnUnlikeClickListener {
        void onUnlikeClick(Song song, int position);
    }

    public FavouriteAdapter(List<Song> favouriteSongs, OnUnlikeClickListener listener) {
        this.favouriteSongs = favouriteSongs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favourite_song_item, parent, false);
        return new FavouriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteViewHolder holder, int position) {
        Song song = favouriteSongs.get(position);
        holder.songTitle.setText(song.getTitle());
        holder.songArtist.setText(song.getArtist());

        try {
            int imageResource = Integer.parseInt(song.getAlbumArtUri());
            Glide.with(holder.itemView.getContext())
                    .load(imageResource)
                    .placeholder(R.drawable.vibe_logo)
                    .into(holder.songThumbnail);
        } catch (NumberFormatException e) {
            holder.songThumbnail.setImageResource(R.drawable.vibe_logo);
        }

        holder.favouriteIcon.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUnlikeClick(song, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return favouriteSongs.size();
    }

    public static class FavouriteViewHolder extends RecyclerView.ViewHolder {
        ImageView songThumbnail;
        TextView songTitle;
        TextView songArtist;
        ImageView favouriteIcon;

        public FavouriteViewHolder(@NonNull View itemView) {
            super(itemView);
            songThumbnail = itemView.findViewById(R.id.songThumbnail);
            songTitle = itemView.findViewById(R.id.songTitle);
            songArtist = itemView.findViewById(R.id.songArtist);
            favouriteIcon = itemView.findViewById(R.id.favouriteIcon);
        }
    }
}
