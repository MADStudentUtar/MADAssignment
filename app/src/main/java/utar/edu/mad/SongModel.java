package utar.edu.mad;

public class SongModel {

    private String artist, songURL, song_img, song_title, karaoke;

    private SongModel(){}

    private SongModel(String artist, String song_img, String karaoke, String song_title, String songURL){
        this.artist = artist;
        this.song_img = song_img;
        this.karaoke = karaoke;
        this.song_title = song_title;
        this.songURL = songURL;
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

//    private String Artist, Image, Karaoke, Title, Url;
//
//    private SongModel(){}
//
//    private SongModel(String artist, String image, String karaoke, String title, String url){
//        this.Artist = artist;
//        this.Image = image;
//        this.Karaoke = karaoke;
//        this.Title = title;
//        this.Url = url;
//    }
//
//    public String getArtist() {
//        return Artist;
//    }
//
//    public void setArtist(String artist) {
//        Artist = artist;
//    }
//
//    public String getImage() {
//        return Image;
//    }
//
//    public void setImage(String image) {
//        Image = image;
//    }
//
//    public String getKaraoke() {
//        return Karaoke;
//    }
//
//    public void setKaraoke(String karaoke) {
//        Karaoke = karaoke;
//    }
//
//    public String getTitle() {
//        return Title;
//    }
//
//    public void setTitle(String title) {
//        Title = title;
//    }
//
//    public String getUrl() {
//        return Url;
//    }
//
//    public void setUrl(String url) {
//        Url = url;
//    }
}
