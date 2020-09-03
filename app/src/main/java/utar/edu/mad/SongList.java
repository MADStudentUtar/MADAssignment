package utar.edu.mad;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class SongList extends AppCompatActivity implements FirestoreAdapter.OnListItemClick {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;

    private RecyclerView firestoreList;
    private FirestoreAdapter adapter;

    String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        firestoreList = findViewById(R.id.firestore_list);

        documentReference = db.collection("user").document(currentUserID);

        //Query songs from firebase
        Query querySong = db.collection("songs").document(currentUserID).collection("songs_details");

        //RecyclerOptions
        FirestoreRecyclerOptions<SongModel> options = new FirestoreRecyclerOptions.Builder<SongModel>()
                .setQuery(querySong, SongModel.class)
                .build();
        adapter = new FirestoreAdapter(options,this);
        firestoreList.setHasFixedSize(true);
        firestoreList.setLayoutManager(new LinearLayoutManager(this));
        firestoreList.setAdapter(adapter);

        //swipe to delete
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {

                    FirestoreAdapter.SongsViewHolder songsViewHolder = (FirestoreAdapter.SongsViewHolder) viewHolder;
                    songsViewHolder.deleteItem();
                }

            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(SongList.this, R.color.colorRedDelete))
                        .addActionIcon(R.drawable.ic_baseline_delete_24)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(firestoreList);

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
                        startActivity(new Intent(SongList.this, ShowProfile.class));
                        finish();
                        return true;
                }
                return false;
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
        String artist = "", karaoke = "", lyrics = "", song_img = "", song_url = "", song_title ="";
        Map<String, Object> list = snapshot.getData();

        ArrayList<String> key = new ArrayList<>();

        Iterator iterator = list.keySet().iterator();
        while(iterator.hasNext()){
             key.add(iterator.next().toString());
        }

        for(int i = 0; i < key.size(); i++) {
            if (key.get(i).equals("artist")){
                artist = (String) list.get(key.get(i));
            } else if (key.get(i).equals("karaoke")) {
                karaoke = (String) list.get(key.get(i));
            } else if (key.get(i).equals("lyrics")) {
                lyrics = (String) list.get(key.get(i));
            } else if (key.get(i).equals("song_img")) {
                song_img = (String) list.get(key.get(i));
            } else if (key.get(i).equals("song_url")) {
                song_url = (String) list.get(key.get(i));
            } else if (key.get(i).equals("song_title")) {
                song_title = (String) list.get(key.get(i));
            }
        }


        Intent intent = new Intent(SongList.this, LyricsDisplay.class);
        intent.putExtra("artist", artist);
        intent.putExtra("karaoke", karaoke);
        intent.putExtra("lyrics", lyrics);
        intent.putExtra("song_img", song_img);
        intent.putExtra("song_url", song_url);
        intent.putExtra("song_title", song_title);
        startActivity(intent);

    }

    // delete item from RecycleView
    @Override
    public void handleDeleteItem(DocumentSnapshot snapshot) {
        final DocumentReference docRef = snapshot.getReference();
        final SongModel songModel = snapshot.toObject(SongModel.class);
        docRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("tag", "onSuccess: Item deleted");
                    }
                });

        Snackbar.make(firestoreList, "Item deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        docRef.set(songModel);
                    }
                }).show();
    }
}