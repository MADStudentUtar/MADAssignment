package utar.edu.mad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CreateProfile extends AppCompatActivity {
    EditText et_name, et_bio, et_birthdate, et_favouritesong;
    Button button;
    ProgressBar progressBar;
    private Uri imageUri;
    private static final int PICK_IMAGE = 1;
    UploadTask uploadTask;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    ImageView imageView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        et_name = findViewById(R.id.et_name_cp);
        et_bio = findViewById(R.id.et_bio_cp);
        et_birthdate = findViewById(R.id.et_birthdate_cp);
        et_favouritesong = findViewById(R.id.et_favouritesong_cp);
        button = findViewById(R.id.btn_cp);
        progressBar = findViewById(R.id.progressbar_cp);
        imageView = findViewById(R.id.iv_cp);

        documentReference = db.collection("user").document(currentUserID);
        storageReference = firebaseStorage.getInstance().getReference("profile images");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadData();
            }
        });
    }

    public void ChooseImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE || resultCode == RESULT_OK || data != null || data.getData() != null){
            imageUri = data.getData();

            Picasso.get().load(imageUri).into(imageView);
        }
    }

    private String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void UploadData(){
        final String name = et_name.getText().toString();
        final String bio = et_bio.getText().toString();
        final String birthdate = et_birthdate.getText().toString();
        final String favouritesong = et_favouritesong.getText().toString();

        if(!TextUtils.isEmpty(name) &&!TextUtils.isEmpty(bio) &&!TextUtils.isEmpty(birthdate) &&!TextUtils.isEmpty(favouritesong) && imageUri != null){
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getFileExt(imageUri));

            uploadTask = reference.putFile(imageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>(){
                @Override
                public Task<Uri> then (@NonNull Task<UploadTask.TaskSnapshot>task) throws Exception{
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                Uri downloadUri = task.getResult();
                                Map<String, String> profile_details = new HashMap<>();
                                profile_details.put("name",name);
                                profile_details.put("bio",bio);
                                profile_details.put("birthdate",birthdate);
                                profile_details.put("favouritesong",favouritesong);
                                profile_details.put("url",downloadUri.toString());

                                documentReference.set(profile_details)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(CreateProfile.this, "Profile Created", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(CreateProfile.this,ShowProfile.class);
                                                startActivity(intent);

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(CreateProfile.this, "failed", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }else {
            Toast.makeText(this, "All Fields Required!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            String name_result = task.getResult().getString("name");
                            String bio_result = task.getResult().getString("bio");
                            String birthdate_result = task.getResult().getString("birthdate");
                            String favouritesong_result = task.getResult().getString("favouritesong");
                            String Url = task.getResult().getString("url");

                            Picasso.get().load(Url).into(imageView);
                            et_name.setText(name_result);
                            et_bio.setText(bio_result);
                            et_birthdate.setText(birthdate_result);
                            et_favouritesong.setText(favouritesong_result);

                        }else{
                            Toast.makeText(CreateProfile.this, "No Profile Exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}