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
import android.util.Log;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class UpdateUser extends AppCompatActivity {
    EditText et_name, et_bio, et_birthdate, et_favouritesong;
    Button button;
    ProgressBar progressBar;
    private Uri imageUri;
    private static final int PICK_IMAGE = 1;
    UploadTask uploadTask;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    ImageView imageView;

    String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    SharedPreferences profileSP;
    SharedPreferences.Editor profileEditor;
    String defaultUrl = "https://www.pngitem.com/pimgs/m/146-1468479_my-profile-icon-blank-profile-picture-circle-hd.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        profileSP = getSharedPreferences("profile", MODE_PRIVATE);
        profileEditor = profileSP.edit();

        et_name = findViewById(R.id.et_name_uu);
        et_bio = findViewById(R.id.et_bio_uu);
        et_birthdate = findViewById(R.id.et_birthdate_uu);
        et_favouritesong = findViewById(R.id.et_favouritesong_uu);
        button = findViewById(R.id.btn_uu);
        progressBar = findViewById(R.id.progressbar_uu);
        imageView = findViewById(R.id.iv_uu);

        String name_result = profileSP.getString("name", "Username");
        String bio_result = profileSP.getString("bio", "Let's Sing!");
        String birthdate_result = profileSP.getString("birthdate", "");
        String favouritesong_result = profileSP.getString("favouritesong", "");
        String url_result = profileSP.getString("url", defaultUrl);

        et_name.setText(name_result);
        et_bio.setText(bio_result);
        et_birthdate.setText(birthdate_result);
        et_favouritesong.setText(favouritesong_result);
        Picasso.get().load(url_result).into(imageView);

        documentReference = db.collection("user").document(currentUserID);
        storageReference = firebaseStorage.getInstance().getReference("profile images");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Updateprofile();
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

    private void Updateprofile(){
        final String name = et_name.getText().toString();
        final String bio = et_bio.getText().toString();
        final String birthdate = et_birthdate.getText().toString();
        final String favouritesong = et_favouritesong.getText().toString();

        if(!TextUtils.isEmpty(name)) {
            progressBar.setVisibility(View.VISIBLE);

            if(imageUri == null) {
                final DocumentReference sfDocRef = db.collection("user").document(currentUserID);

                db.runTransaction(new Transaction.Function<Void>() {
                    @Override
                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(sfDocRef);

                        //transaction.update(sfDocRef, "population", newPopulation);
                        transaction.update(sfDocRef,"name", name);
                        transaction.update(sfDocRef, "searchName", name.toLowerCase());
                        transaction.update(sfDocRef,"bio", bio);
                        transaction.update(sfDocRef,"birthdate", birthdate);
                        transaction.update(sfDocRef,"favouritesong", favouritesong);

                        profileEditor.putString("name", name);

                        if(!TextUtils.isEmpty(bio)) {
                            profileEditor.putString("bio", bio);
                        } else {
                            profileEditor.remove("bio");
                        }

                        if(!TextUtils.isEmpty(birthdate)) {
                            profileEditor.putString("birthdate", birthdate);
                        } else {
                            profileEditor.remove("birthdate");
                        }

                        if(!TextUtils.isEmpty(favouritesong)) {
                            profileEditor.putString("favouritesong", favouritesong);
                        } else {
                            profileEditor.remove("favouritesong");
                        }

                        profileEditor.commit();

                        return null;
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UpdateUser.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
            else {
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
                            final Uri downloadUri = task.getResult();
                            final DocumentReference sfDocRef = db.collection("user").document(currentUserID);

                            db.runTransaction(new Transaction.Function<Void>() {
                                @Override
                                public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                    DocumentSnapshot snapshot = transaction.get(sfDocRef);

                                    //transaction.update(sfDocRef, "population", newPopulation);
                                    transaction.update(sfDocRef,"name",name);
                                    transaction.update(sfDocRef,"searchName", name.toLowerCase());
                                    transaction.update(sfDocRef,"bio",bio);
                                    transaction.update(sfDocRef,"birthdate",birthdate);
                                    transaction.update(sfDocRef,"favouritesong",favouritesong);
                                    transaction.update(sfDocRef,"url",downloadUri.toString());

                                    profileEditor.putString("name", name);
                                    profileEditor.putString("url", downloadUri.toString());

                                    if(!TextUtils.isEmpty(bio)) {
                                        profileEditor.putString("bio", bio);
                                    } else {
                                        profileEditor.remove("bio");
                                    }

                                    if(!TextUtils.isEmpty(birthdate)) {
                                        profileEditor.putString("birthdate", birthdate);
                                    } else {
                                        profileEditor.remove("birthdate");
                                    }

                                    if(!TextUtils.isEmpty(favouritesong)) {
                                        profileEditor.putString("favouritesong", favouritesong);
                                    } else {
                                        profileEditor.remove("favouritesong");
                                    }

                                    profileEditor.commit();


                                    return null;
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(UpdateUser.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

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
            }

        } else {
            Toast.makeText(this, "Username field is required!", Toast.LENGTH_SHORT).show();
        }
    }
}