package utar.edu.mad;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class LyricsDisplay extends AppCompatActivity {

    MediaPlayer song;
    MediaPlayer karaoke;

    ImageButton btn;
    ImageButton sourceBtn;
    ImageButton recordBtn;

    SeekBar positionBar;

    TextView elapsedTimeLabel;
    TextView remainingTimeLabel;
    TextView songTitleLabel;
    TextView artistLabel;

    int totalTime;

    boolean karaokeFlag = false;

    LinearLayout lyricsDisplay;
    LinearLayout artistDisplay;

    //For synchronization
    ArrayList<String> time;
    ArrayList<String> lyrics;

    //Future intent
    String lyricsURL = "https://firebasestorage.googleapis.com/v0/b/karaokie-7aaa8.appspot.com/o/lyrics%2FJust%20The%20Way%20You%20Are.txt?alt=media&token=becfc794-7a00-47bf-ae78-59b522ab65fe";
    String artistURL = "https://firebasestorage.googleapis.com/v0/b/karaokie-7aaa8.appspot.com/o/songsImg%2FBruno%20Mars.jpg?alt=media&token=3a0a3ee2-8e57-4725-a842-33c7a8a8bed7";
    String songURL = "https://firebasestorage.googleapis.com/v0/b/karaokie-7aaa8.appspot.com/o/songs%2FJust%20the%20Way%20You%20Are.mp3?alt=media&token=68b243a4-d91d-4869-a115-57b32d869bfb";
    String karaokeURL = "https://firebasestorage.googleapis.com/v0/b/karaokie-7aaa8.appspot.com/o/songs%2FJust%20the%20Way%20You%20Are%20-%20Karaoke%20ver.mp3?alt=media&token=f480e4ac-93ee-4347-8c2e-4d675df889c5";
    String songTitle = "Just The Way You Are";
    String artist = "Bruno Mars";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyrics_display);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //setup screen bg with artist
        Bitmap artistBg = getBitmapFromURL(artistURL);
        Drawable dr = new BitmapDrawable(artistBg);
        artistDisplay = findViewById(R.id.bg);
        artistDisplay.setBackground(dr);

        //set music info display
        songTitleLabel = findViewById(R.id.songTitle);
        songTitleLabel.setText(songTitle);
        artistLabel = findViewById(R.id.artist);
        artistLabel.setText(artist);

        //get duration label
        elapsedTimeLabel = (TextView) findViewById(R.id.elapsedTimeLabel);
        remainingTimeLabel = (TextView) findViewById(R.id.remainingTimeLabel);

        //get song media
        song = new MediaPlayer();
        //get karaoke media
        karaoke = new MediaPlayer();

        try {
            song.setDataSource(songURL);
            song.prepare();
            karaoke.setDataSource(karaokeURL);
            karaoke.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //reset to 0
        song.seekTo(0);
        karaoke.seekTo(0);

        //get total time
        totalTime = song.getDuration();

        //positionBar
        positionBar = (SeekBar) findViewById(R.id.positionBar);
        positionBar.setMax(totalTime);
        positionBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            song.seekTo(progress);
                            karaoke.seekTo(progress);
                            positionBar.setProgress(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );

        //read lyrics
        AssetManager am = this.getAssets();
        lyricsDisplay = findViewById(R.id.lyrics);
        String timeRegex = "\\[([0-9]+):([0-9.]+)\\]";

        try {
            URL url = new URL(lyricsURL);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;

            time = new ArrayList<>();
            lyrics = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                //add time tag into arraylist
                time.add(line.split(timeRegex)[0]);

                //add lyrics to arraylist
                lyrics.add(line.split(timeRegex)[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //read lyrics end

        //update position bar
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (song != null) {
                    try {
                        Message msg = new Message();
                        msg.what = song.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();

        //generate lyrics display
        for (int i = 0; i < lyrics.size(); i++) {
            TextView tv = new TextView(this);

            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(16);
            tv.setTextColor(Color.WHITE);
            tv.setText(lyrics.get(i));

            lyricsDisplay.addView(tv);
        }

        //play the song
        song.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            // Update positionBar.
            positionBar.setProgress(currentPosition);

            // Update Labels.
            String elapsedTime = createTimeLabel(currentPosition);
            elapsedTimeLabel.setText(elapsedTime);

            String remainingTime = createTimeLabel(totalTime - currentPosition);
            remainingTimeLabel.setText("- " + remainingTime);

            //Update Lyrics should goes here

        }
    };

    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }

    public void playBtnClick(View view) {

        btn = findViewById(R.id.controller);

        if (!karaokeFlag)
            //normal song play pause
            if (!song.isPlaying()) {
                // Stopping
                song.start();
                btn.setImageResource(R.drawable.pause);

            } else {
                // Playing
                song.pause();
                btn.setImageResource(R.drawable.play);
            }

        if (karaokeFlag)
            if (!karaoke.isPlaying()) {
                karaoke.start();
                song.start();
                btn.setImageResource(R.drawable.pause);
            } else {
                karaoke.pause();
                song.pause();
                btn.setImageResource(R.drawable.play);
            }
    }

    public void onOffKaraoke(View view) {
        sourceBtn = findViewById(R.id.source);

        if(!song.isPlaying()){
            Toast.makeText(this, "Please start the music", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!karaoke.isPlaying()) {
            karaoke.seekTo(song.getCurrentPosition());
            karaoke.start();
            //mute playing song
            song.setVolume(0, 0);
            sourceBtn.setImageResource(R.drawable.on);

            Toast.makeText(this, "Karaoke Mode", Toast.LENGTH_SHORT).show();
            karaokeFlag = true;
        } else if (karaoke.isPlaying() && song.isPlaying()) {
            song.seekTo(karaoke.getCurrentPosition());
            //unmute playing song
            song.setVolume(1, 1);
            karaoke.pause();
            sourceBtn.setImageResource(R.drawable.off);

            Toast.makeText(this, "Music Mode", Toast.LENGTH_SHORT).show();
            karaokeFlag = false;
        }
    }

    public Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}