package utar.edu.mad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecordeAudio extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 1;

    ArrayList<String> title;
    ArrayList<String> description;
    ArrayList<String> filepath;

    ListView recordList;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorde_audio);

        //get permission
        if (ContextCompat.checkSelfPermission(RecordeAudio.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(RecordeAudio.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(RecordeAudio.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }else {
                ActivityCompat.requestPermissions(RecordeAudio.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        } else
            getMusic();

        adapter = new MyAdapter(this, title, description);
        recordList = findViewById(R.id.recordedList);
        recordList.setAdapter(adapter);
        recordList.setEmptyView(findViewById(R.id.emptyText));

        recordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent i1 = new Intent(RecordeAudio.this, LyricsDisplay.class);

                i1.putExtra("song_url", filepath.get(i));
                i1.putExtra("song_title", title.get(i));
                i1.putExtra("artist", description.get(i));
                i1.putExtra("records", true);

                startActivity(i1);
            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void getMusic(){

        title = new ArrayList<>();
        description = new ArrayList<>();
        filepath = new ArrayList<>();

        File folder = new File(getExternalCacheDir().getAbsolutePath() + File.separator);

        if(folder.exists()){
            for(File f : folder.listFiles()){
                String filename = f.getName();
                title.add(filename.substring(0, 12));
                description.add(filename.substring(13, 21));
                filepath.add(f.getAbsolutePath());
            }
        }
    }

    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        ArrayList<String> rTitle;
        ArrayList<String> rDescription;

        MyAdapter (Context c, ArrayList<String> title, ArrayList<String> description) {
            super(c, R.layout.row, R.id.songTitle, title);
            this.context = c;
            this.rTitle = title;
            this.rDescription = description;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row, parent, false);

            TextView myTitle = row.findViewById(R.id.song_title);
            TextView myDescription = row.findViewById(R.id.song_artist);
            ImageView myImage = row.findViewById(R.id.song_image);

            // now set our resources on views
            myTitle.setText(rTitle.get(position));
            myDescription.setText(rDescription.get(position));
            myImage.setImageResource(R.drawable.audiotrack);

            return row;
        }
    }
}