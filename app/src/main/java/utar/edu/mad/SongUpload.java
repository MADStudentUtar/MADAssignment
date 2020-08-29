package utar.edu.mad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class SongUpload extends AppCompatActivity {

    TextView songURL;
    ProgressBar progressBar;
    Uri audioUri;
    StorageReference storageReference;
    StorageTask storageTask;
    DatabaseReference referenceSongs;
    MediaMetadataRetriever metadataRetriever;
    EditText editTitle, editArtist;
    byte[] art;
    String title, artist, album_art="", duration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_upload);

        songURL = findViewById(R.id.songURL);
        progressBar = findViewById(R.id.uploadProgress);
        editTitle = findViewById(R.id.songName);
        editArtist = findViewById(R.id.singerName);

        metadataRetriever = new MediaMetadataRetriever();
        referenceSongs = FirebaseDatabase.getInstance().getReference().child("songs");
        storageReference = FirebaseStorage.getInstance().getReference().child("songs");
    }

    public void openAudioFiles (View v) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("audio/*");
        startActivityForResult(i, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== 101 && resultCode == RESULT_OK && data.getData() != null){
            audioUri = data.getData();
            String fileNames = getFileName(audioUri);
            songURL.setText(fileNames);
            metadataRetriever.setDataSource(this,audioUri);

            artist = editArtist.getText().toString();
            title = editTitle.getText().toString();
            art = metadataRetriever.getEmbeddedPicture();
        }
    }

    private String getFileName(Uri uri){
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
            finally {
                cursor.close();
            }
        }
        if(result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1){
                result = result.substring(cut +1);
            }
        }
        return result;
    }

    public void uploadFirebase (View v){
        if(songURL.equals("No file selected")) {
            Toast.makeText(this, "Please upload song!", Toast.LENGTH_SHORT).show();
        } else{
            if(storageTask != null && storageTask.isInProgress()){
                Toast.makeText(this, "Song uploads in progress.",Toast.LENGTH_SHORT).show();

            } else {
                uploadFiles();
            }
        }
    }

    private void uploadFiles () {
        if(audioUri != null){
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference storageReference1 = storageReference.child(System.currentTimeMillis()+"."+getfileextension(audioUri));
            storageTask = storageReference1.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Upload uploadSong = new Upload(title, artist, album_art, duration, audioUri.toString());
                            String uploadId = referenceSongs.push().getKey();
                            referenceSongs.child(uploadId).setValue(uploadSong);
                            Toast.makeText(getApplicationContext(), "Song uploaded.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), SongList.class));
                            finish();
                        }
                    });

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int)progress);
                }
            });
        } else {
            Toast.makeText(this, "No file selected to upload", Toast.LENGTH_SHORT).show();
        }
    }

    private String getfileextension (Uri audioUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(audioUri));
    }
}