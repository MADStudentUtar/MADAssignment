package utar.edu.mad;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Chat extends AppCompatActivity {
    RecyclerView chatsWrapper;
    ProgressBar progressBar;
    ChatAdapter chatAdapter;

    // Firebase variable
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference receiversCollection;
    String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatsWrapper = (RecyclerView) findViewById(R.id.chatsWrapperView);
        chatsWrapper.setHasFixedSize(true);
        chatsWrapper.setLayoutManager(new LinearLayoutManager(this));

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Get chats from firebase
        receiversCollection = db.collection("messages").document(currentUserID).collection("receivers");
        Query q = receiversCollection.orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatModel> options = new FirestoreRecyclerOptions.Builder<ChatModel>().setQuery(q, ChatModel.class).build();

        chatAdapter = new ChatAdapter(options);
        chatsWrapper.setAdapter(chatAdapter);

        progressBar.setVisibility(View.GONE);

        chatAdapter.setOnClickListener(new ChatAdapter.onClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String id = documentSnapshot.getId();
                String name = (String) documentSnapshot.get("name");
                String url = (String) documentSnapshot.get("url");

                Intent intent = new Intent(Chat.this, Message.class);
                intent.putExtra("Id", id);
                intent.putExtra("Name", name);
                intent.putExtra("Url", url);
                startActivity(intent);
            }
        });

        //Bottom navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        //Set home page selected
        bottomNavigationView.setSelectedItemId(R.id.chat);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.chat:
                        return true;
                    case R.id.songList:
                        startActivity(new Intent(Chat.this, SongList.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.friendList:
                        startActivity(new Intent(Chat.this, Friend.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(Chat.this, ShowProfile.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        chatAdapter.startListening();
    }

    @Override
    protected void onStop(){
        super.onStop();
        chatAdapter.stopListening();
    }
}