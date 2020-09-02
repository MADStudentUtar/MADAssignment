package utar.edu.mad;

import java.util.ArrayList;
import java.util.List;

public class SongModel {

    private String artist, songURL, song_img, song_title, karaoke, lyrics;
    private List<String> keyword;

    private SongModel() {
    }

    private SongModel(String artist, String song_img, String karaoke, String lyrics, String song_title, String songURL, List<String> keyword) {
        this.artist = artist;
        this.song_img = song_img;
        this.karaoke = karaoke;
        this.lyrics = lyrics;
        this.song_title = song_title;
        this.songURL = songURL;
        this.keyword = keyword;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSongURL() {
        return songURL;
    }

    public void setSongURL(String songURL) {
        this.songURL = songURL;
    }

    public String getSong_img() {
        return song_img;
    }

    public void setSong_img(String song_img) {
        this.song_img = song_img;
    }

    public String getSong_title() {
        return song_title;
    }

    public void setSong_title(String song_title) {
        this.song_title = song_title;
    }

    public String getKaraoke() {
        return karaoke;
    }

    public void setKaraoke(String karaoke) {
        this.karaoke = karaoke;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public List<String> getKeyword() {
        return keyword;
    }

    public void setKeyword(List<String> keyword) {
        this.keyword = keyword;
    }
}
