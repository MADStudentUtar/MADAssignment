package utar.edu.mad;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class SongList extends AppCompatActivity implements FirestoreAdapter.OnListItemClick {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    Button searchBtn;

    private RecyclerView firestoreList;
    private FirestoreAdapter adapter;

    String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        firestoreList = findViewById(R.id.firestore_list);

        documentReference = db.collection("user").document(currentUserID).collection("profile").document("profile_details");

        //Query songs from firebase
        Query querySong = db.collection("user").document(currentUserID).collection("songs");

        //RecyclerOptions
        FirestoreRecyclerOptions<SongModel> options = new FirestoreRecyclerOptions.Builder<SongModel>()
                .setQuery(querySong, SongModel.class)
                .build();
        adapter = new FirestoreAdapter(options,this);
        firestoreList.setHasFixedSize(true);
        firestoreList.setLayoutManager(new LinearLayoutManager(this));
        firestoreList.setAdapter(adapter);

        //search button
        searchBtn = findViewById(R.id.searchBtn);

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
                        startActivity(new Intent(SongList.this, Chat.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.profile:
                        ShowProfile();
                        //finish();
                        return true;
                }
                return false;
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