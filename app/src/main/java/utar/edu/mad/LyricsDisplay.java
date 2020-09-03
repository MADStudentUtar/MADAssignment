package utar.edu.mad;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LyricsDisplay extends AppCompatActivity {

    MediaPlayer song = null;
    MediaPlayer karaoke = null;
    MediaRecorder recorder = null;

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
    boolean recordStart = false;

    LinearLayout lyricsDisplay;
    LinearLayout artistDisplay;

    //For synchronization
    ArrayList<String> time;
    ArrayList<String> lyrics;

    String fileName = null;
    String[] permissions = {android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //initialization
    String lyricsURL = "";
    String songImgURL = "";
    String songURL = "";
    String karaokeURL = "";
    String songTitle = "";
    String artist = "";
    boolean recordsFlag = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyrics_display);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ActivityCompat.requestPermissions(this, permissions, 200);

        //getting parameter
        //songURL = this.getIntent().getStringExtra("song_url");
        //karaokeURL = this.getIntent().getStringExtra("karaoke");
        //songTitle = this.getIntent().getStringExtra("song_title");
        //artist = this.getIntent().getStringExtra("artist");
        //lyricsURL = this.getIntent().getStringExtra("lyrics");
        //songImgURL = this.getIntent().getStringExtra("song_img");
        //recordsFlag = this.getIntent().getBooleanExtra("records", false);


        //setup screen bg with artist
        Bitmap artistBg = getBitmapFromURL(songImgURL);
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
            //local
            if(recordsFlag)
                song.setDataSource("file://" + songURL);
            //online
            else
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

                TextView tv = new TextView(this);

                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(16);
                tv.setTextColor(Color.WHITE);

                tv.setText(line.split(timeRegex)[1]);
                lyricsDisplay.addView(tv);
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

        //play the song
        song.start();

        //set up records filename
        Date cur = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");

        fileName = getExternalCacheDir().getAbsolutePath() + File.separator;
        fileName += format.format(cur);
        fileName += ".3gp";

        //disable button if playing recorded song
        if(recordsFlag){
            findViewById(R.id.record).setEnabled(false);
            findViewById(R.id.source).setEnabled(false);
        }

        //exit player
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        song.release();
        song = null;
        karaoke.release();
        finish();
    }

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

        if (!song.isPlaying()) {
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

    //record voice
    private void startRecording() {
        recordBtn = findViewById(R.id.record);

        recorder = new MediaRecorder();
        recorder.reset();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        recordBtn.setImageResource(R.drawable.offmic);
        Toast.makeText(this, "Recording Start", Toast.LENGTH_SHORT).show();

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("Audio Record", "prepare failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        Toast.makeText(this, "Recording Stop", Toast.LENGTH_SHORT).show();
        recordBtn.setImageResource(R.drawable.onmic);
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    public void onRecord(View view) {

        if (recordStart)
            stopRecording();
        else
            startRecording();

        recordStart = !recordStart;
    }

}