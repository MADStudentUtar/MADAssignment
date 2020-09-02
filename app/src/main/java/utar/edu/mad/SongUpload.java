package utar.edu.mad;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class SongUpload extends AppCompatActivity {

    TextView songURL;
    ProgressBar progressBar;
    Uri audioUri, album_art;
    StorageReference storageReference, storageReferenceImg;
    StorageTask storageTask;
    DocumentReference documentReference;
    MediaMetadataRetriever metadataRetriever;
    String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText editTitle, editArtist;
    byte[] art;
    String title, artist;
    String defaultImg = "https://firebasestorage.googleapis.com/v0/b/karaokie-7aaa8.appspot.com/o/songsImg%2Fblack.png?alt=media&token=ea37ee3d-1883-4def-aee6-abde42b12f43";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_upload);

        songURL = findViewById(R.id.songURL);
        progressBar = findViewById(R.id.uploadProgress);
        editTitle = findViewById(R.id.songName);
        editArtist = findViewById(R.id.singerName);

        metadataRetriever = new MediaMetadataRetriever();
        storageReference = FirebaseStorage.getInstance().getReference("songs");
        storageReferenceImg = FirebaseStorage.getInstance().getReference("songsImg");
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
        artist = editArtist.getText().toString();
        title = editTitle.getText().toString();
        generateSearchKeywords generateKeywords = new generateSearchKeywords();
        final List<String> keyword = generateKeywords.searchKeywords(title);
        if(artist.equals("") ||  title.equals("")){
            Toast.makeText(this, "Please fill in all details.", Toast.LENGTH_SHORT).show();
        } else {
            if (audioUri != null) {
                progressBar.setVisibility(View.VISIBLE);
                if (art != null) {
                    final StorageReference ImgName = storageReferenceImg.child(title + "_" + artist + ".jpg");
                    UploadTask uploadTask = ImgName.putBytes(art);

                    Task<Uri> tasks = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful()){
                                throw task.getException();
                            }

                            return ImgName.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                Uri downloadUri = task.getResult();
                                album_art = downloadUri;
                            }
                        }
                    });
                }

                final StorageReference storageReference1 = storageReference.child(System.currentTimeMillis() + "." + getfileextension(audioUri));
                storageTask = storageReference1.putFile(audioUri);
                Task urlTask = storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return storageReference1.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            Map<String, Object> song_details = new HashMap<>();
                            song_details.put("keyword", keyword);
                            song_details.put("karaoke", "");
                            song_details.put("lyrics", "");
                            song_details.put("song_title", title);
                            song_details.put("artist", artist);
                            song_details.put("songURL", downloadUri.toString());
                            if (album_art != null) {
                                song_details.put("song_img",album_art.toString());
                            } else {
                                song_details.put("song_img",defaultImg);
                            }


                            documentReference = db.collection("user").document(currentUserID).collection("songs").document(title+"-"+artist);

                            documentReference.set(song_details)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(getApplicationContext(), "Song uploaded", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), SongList.class));
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Failed to upload. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            } else {
                Toast.makeText(this, "Please upload song.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getfileextension (Uri audioUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(audioUri));
    }
}