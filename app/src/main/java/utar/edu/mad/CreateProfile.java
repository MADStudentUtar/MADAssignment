package utar.edu.mad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private Uri imageUri = null;
    private static final int PICK_IMAGE = 1;
    UploadTask uploadTask;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    ImageView imageView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    SharedPreferences profileSP;
    SharedPreferences.Editor profileEditor;
    String defaultUrl = "https://www.pngitem.com/pimgs/m/146-1468479_my-profile-icon-blank-profile-picture-circle-hd.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        profileSP = getSharedPreferences("profile", MODE_PRIVATE);
        profileEditor = profileSP.edit();

        et_name = findViewById(R.id.et_name_cp);
        et_bio = findViewById(R.id.et_bio_cp);
        et_birthdate = findViewById(R.id.et_birthdate_cp);
        et_favouritesong = findViewById(R.id.et_favouritesong_cp);
        button = findViewById(R.id.btn_cp);
        progressBar = findViewById(R.id.progressbar_cp);
        imageView = findViewById(R.id.iv_cp);

        Picasso.get().load(defaultUrl).into(imageView);

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

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
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

        if(!TextUtils.isEmpty(name)){
            progressBar.setVisibility(View.VISIBLE);

            if(imageUri == null) {
                Map<String, String> profile_details = new HashMap<>();
                profile_details.put("name", name);
                profile_details.put("searchName", name.toLowerCase());
                profile_details.put("url", defaultUrl);

                profileEditor.putString("name", name);
                profileEditor.putString("url", defaultUrl);

                if(!TextUtils.isEmpty(bio)) {
                    profile_details.put("bio", bio);
                    profileEditor.putString("bio", bio);
                }

                if(!TextUtils.isEmpty(birthdate)) {
                    profile_details.put("birthdate", birthdate);
                    profileEditor.putString("birthdate", birthdate);
                }

                if(!TextUtils.isEmpty(favouritesong)) {
                    profile_details.put("favouritesong", favouritesong);
                    profileEditor.putString("favouritesong", favouritesong);
                }

                profileEditor.commit();

                documentReference.set(profile_details)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(CreateProfile.this, "Profile Created", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateProfile.this, "failed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
            }
            else {
                final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExt(imageUri));

                uploadTask = reference.putFile(imageUri);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return reference.getDownloadUrl();
                    }
                })
                        .addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    Map<String, String> profile_details = new HashMap<>();
                                    profile_details.put("name", name);
                                    profile_details.put("searchName", name.toLowerCase());
                                    profile_details.put("url", downloadUri.toString());

                                    profileEditor.putString("name", name);
                                    profileEditor.putString("url", downloadUri.toString());

                                    if(!TextUtils.isEmpty(bio)) {
                                        profile_details.put("bio", bio);
                                        profileEditor.putString("bio", bio);
                                    }

                                    if(!TextUtils.isEmpty(birthdate)) {
                                        profile_details.put("birthdate", birthdate);
                                        profileEditor.putString("birthdate", birthdate);
                                    }

                                    if(!TextUtils.isEmpty(favouritesong)) {
                                        profile_details.put("favouritesong", favouritesong);
                                        profileEditor.putString("favouritesong", favouritesong);
                                    }

                                    profileEditor.commit();

                                    documentReference.set(profile_details)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(CreateProfile.this, "Profile Created", Toast.LENGTH_SHORT).show();
                                            finish();
                                            return;
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(CreateProfile.this, "failed", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    });
                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("Failed");
                                e.printStackTrace();
                            }
                        });
            }
        } else {
            Toast.makeText(this, "Username field is required!", Toast.LENGTH_SHORT).show();
        }
    }
}