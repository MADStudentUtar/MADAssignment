package utar.edu.mad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import static utar.edu.mad.R.menu.menu_friend;


public class Friend extends AppCompatActivity{

    RecyclerView recyclerView;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference friendRef = db.collection("user");
    private FriendAdapter adapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(menu_friend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                startActivity(new Intent(Friend.this, SearchFriend.class));
                overridePendingTransition(0, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        //toolbar
        Toolbar toolbar = findViewById(R.id.friendToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Friend List");

        //recyclerView
        setUpRecyclerView();

        //Bottom navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        //Set home page selected
        bottomNavigationView.setSelectedItemId(R.id.friendList);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.friendList:
                        return true;
                    case R.id.songList:
                        startActivity(new Intent(Friend.this, SongList.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.chat:
                        startActivity(new Intent(Friend.this, Chat.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(Friend.this, ShowProfile.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

    private void setUpRecyclerView(){

        //query
        final Query query = friendRef.orderBy("name", Query.Direction.ASCENDING);

        //recyclerOption
        FirestoreRecyclerOptions<FindFriend> options = new FirestoreRecyclerOptions.Builder<FindFriend>()
                .setQuery(query, FindFriend.class)
                .build();

        adapter = new FriendAdapter(options);

        //RecyclerView
        recyclerView = findViewById(R.id.contacts_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnClickListener(new FriendAdapter.onClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                FindFriend findFriend = documentSnapshot.toObject(FindFriend.class);

                String id = documentSnapshot.getId();
                String name = documentSnapshot.get("name").toString();
                String bio = documentSnapshot.get("bio").toString();
                String url = documentSnapshot.get("url").toString();
                String birthdate = documentSnapshot.get("birthdate").toString();
                String favouritesong = documentSnapshot.get("favouritesong").toString();

                Intent intent = new Intent(Friend.this, Profile.class);

                intent.putExtra("Id", id);
                intent.putExtra("Name", name);
                intent.putExtra("Bio", bio);
                intent.putExtra("Url", url);
                intent.putExtra("Birthdate", birthdate);
                intent.putExtra("Favouritesong", favouritesong);

                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop(){
        super.onStop();
        adapter.stopListening();
    }
}