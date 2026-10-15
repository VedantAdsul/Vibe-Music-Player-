package com.vedant.vibe.Model;

public class Song {
    private String id;
    private String title;
    private String artist;
    private String path;
    private String albumArtUri;
    private boolean isFavourite;

    public Song(String id, String title, String artist, String path, String albumArtUri, boolean isFavourite) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.path = path;
        this.albumArtUri = albumArtUri;
        this.isFavourite = isFavourite;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAlbumArtUri() {
        return albumArtUri;
    }

    public void setAlbumArtUri(String albumArtUri) {
        this.albumArtUri = albumArtUri;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }
}