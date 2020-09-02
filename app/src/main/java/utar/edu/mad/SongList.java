package utar.edu.mad;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class SongList extends AppCompatActivity implements FirestoreAdapter.OnListItemClick {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    Button searchBtn;
    EditText searchSong;

    private RecyclerView firestoreList;
    private FirestoreAdapter adapter;

    String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    //Query songs from firebase
    Query querySong = db.collection("user").document(currentUserID).collection("songs");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        firestoreList = findViewById(R.id.firestore_list);

        documentReference = db.collection("user").document(currentUserID).collection("profile").document("profile_details");

        //RecyclerOptions
        FirestoreRecyclerOptions<SongModel> options = new FirestoreRecyclerOptions.Builder<SongModel>()
                .setQuery(querySong, SongModel.class)
                .build();
        adapter = new FirestoreAdapter(options,this);
        firestoreList.setHasFixedSize(true);
        firestoreList.setLayoutManager(new LinearLayoutManager(this));
        firestoreList.setAdapter(adapter);

        //search
        searchSong = findViewById(R.id.searchSong);
        searchBtn = findViewById(R.id.searchBtn);
        searchSong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()){
                    search(editable.toString());
                } else {
                    search ("");
                }

            }
        });

        //floating upload button
        FloatingActionButton fab = findViewById(R.id.btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SongList.this, SongUpload.class));
            }
        });

        //Bottom navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        //Set bottom navigation selected button for the page
        bottomNavigationView.setSelectedItemId(R.id.songList);

        //Perform ItemSelectedListener in Navigation Bar
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.songList:
                        return true;
                    case R.id.friendList:
                        startActivity(new Intent(SongList.this, Friend.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.chat:
//                        startActivity(new Intent(getApplicationContext(), ));
//                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
                        ShowProfile();
                        return true;
                }
                return false;
            }
        });
    }

    private void search(String s) {
        Query query = querySong.whereArrayContains("keyword", s);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void ShowProfile(){
        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            Intent intent = new Intent(SongList.this,ShowProfile.class);
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(SongList.this,CreateProfile.class);
                            startActivity(intent);
                        }
                    }
                });
    }


    @Override
    protected void onStop(){
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        //change bottom navigation to song
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.songList);
    }

    // click on the item on recyclerView
    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {
        Log.d("ITEM_CLICK", "Clicked an item:" + position);
        Log.d("Reference", String.valueOf(snapshot.getData()));

    }
}