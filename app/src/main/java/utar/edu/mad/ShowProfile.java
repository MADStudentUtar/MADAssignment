package utar.edu.mad;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ShowProfile extends AppCompatActivity {
    UploadTask uploadTask;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    ImageView imageView;
    TextView nameEt, bioEt, birthdateEt, favouritesongEt;
    Button edit;
    private FirebaseAuth mAuth;

    String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);

        edit = findViewById(R.id.editAcc);
        imageView = findViewById(R.id.imageView_sp);
        documentReference = db.collection("user").document(currentUserID).collection("profile").document("profile_details");
        storageReference = firebaseStorage.getInstance().getReference("profile images");

        nameEt = findViewById(R.id.name_tv_sp);
        bioEt = findViewById(R.id.bio_tv_sp);
        birthdateEt = findViewById(R.id.birthdate_tv_sp);
        favouritesongEt = findViewById(R.id.favouritesong_tv_sp);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowProfile.this,UpdateUser.class);
                startActivity(intent);

            }
        });

        mAuth = FirebaseAuth.getInstance();
        Button button = findViewById(R.id.signoutbutton2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(ShowProfile.this,login.class);
                startActivity(intent);
                finish();
            }
        });

        //Bottom navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        //Set home page selected
        bottomNavigationView.setSelectedItemId(R.id.profile);

        //Perform ItemSelectedListener in Navigation Bar
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.songList:
                        startActivity(new Intent(ShowProfile.this, SongList.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.friendList:
                        startActivity(new Intent(ShowProfile.this, Friend.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.chat:
//                        startActivity(new Intent(getApplicationContext(), ));
//                        overridePendingTransition(0,0);
//                        return true;
                    case R.id.profile:
                        return true;
                }
                return false;
            }
        });

        findViewById(R.id.recordedSong).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShowProfile.this, LyricsDisplay.class));
            }
        });
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
                            nameEt.setText(name_result);
                            bioEt.setText(bio_result);
                            birthdateEt.setText(birthdate_result);
                            favouritesongEt.setText(favouritesong_result);

                        }else{
                            Toast.makeText(ShowProfile.this, "No Profile Exist", Toast.LENGTH_SHORT).show();
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