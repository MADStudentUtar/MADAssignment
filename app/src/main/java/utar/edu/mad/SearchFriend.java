package utar.edu.mad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;;

public class SearchFriend extends AppCompatActivity {

    private ImageButton searchFriendButton;
    private EditText searchFriendText;
    private RecyclerView searchFriendList;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference friendRef = db.collection("user");
    private FriendAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);

        //toolbar
        Toolbar toolbar = findViewById(R.id.searchFriendToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Search Friend");

        //recyclerView
        searchFriendList = (RecyclerView) findViewById(R.id.searchFriend_recycler);
        searchFriendList.setHasFixedSize(true);
        searchFriendList.setLayoutManager(new LinearLayoutManager(this));

        searchFriendButton = (ImageButton) findViewById(R.id.searchFriendBtn);
        searchFriendText = (EditText) findViewById(R.id.searchFriendET);

        searchFriendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                String searchFriendInput = searchFriendText.getText().toString();
                searchFriend(searchFriendInput);
            }
        });
    }

    private void searchFriend(String searchFriendInput)
    {
        Toast.makeText(this, "Searching...", Toast.LENGTH_LONG).show();

        //query
        Query query = friendRef.orderBy("name")
                .startAt(searchFriendInput).endAt(searchFriendInput + "\uf8ff");

        //recyclerOption
        FirestoreRecyclerOptions<FindFriend> options = new FirestoreRecyclerOptions.Builder<FindFriend>()
                .setQuery(query, FindFriend.class)
                .build();

        //recyclerAdapter
        adapter = new FriendAdapter(options);

        searchFriendList.setAdapter(adapter);
        adapter.startListening();

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

                Intent intent = new Intent(SearchFriend.this, Profile.class);

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
}