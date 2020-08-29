package utar.edu.mad;

public class Upload {

    public  String songTitle, artist, album_art, songDuration, songLink, mKey;

    public Upload(String songTitle, String artist, String album_art, String songDuration, String songLink) {
        if(album_art.trim().equals("")){
            album_art = "";
        }

        this.songTitle = songTitle;
        this.artist = artist;
        this.album_art = album_art;
        this.songLink = songLink;

    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum_art() {
        return album_art;
    }

    public void setAlbum_art(String album_art) {
        this.album_art = album_art;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public String getSongLink() {
        return songLink;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }

    public String getmKey() {
        return mKey;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }
}
