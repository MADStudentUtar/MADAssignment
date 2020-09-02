package utar.edu.mad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class NewFriend extends AppCompatActivity {

    private ImageButton searchFriendButton;
    private EditText searchFriendText;

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView searchFriendList;

    private  FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);

        //toolbar
        Toolbar toolbar = findViewById(R.id.addContactsToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Add Contacts");

        firebaseFirestore = FirebaseFirestore.getInstance();

        //recyclerView
        searchFriendList = (RecyclerView) findViewById(R.id.searchFriend_recycler);

        searchFriendButton = (ImageButton) findViewById(R.id.searchFriendBtn);
        searchFriendText = (EditText) findViewById(R.id.searchFriendET);
        searchFriendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                String searchFriendET = searchFriendText.getText().toString();
                searchFriend(searchFriendET);
            }
        });
    }

    private void searchFriend(String searchFriendET)
    {
        Toast.makeText(this, "Searching...", Toast.LENGTH_LONG).show();

        //query
        Query query = firebaseFirestore.collection("user").document()
                .collection("profile").orderBy("name")
                .startAt(searchFriendET).endAt(searchFriendET+ "\uf8ff") ;

        //query
//        Task<DocumentSnapshot> documentSnapshotTask = firebaseFirestore.collection("user").document()
//                .collection("profile").document("profile_details").get();

        //recyclerOption
        FirestoreRecyclerOptions<FindFriend> options = new FirestoreRecyclerOptions.Builder<FindFriend>()
                .setQuery(query, FindFriend.class)
                .build();

        //recyclerOption
//        FirestoreRecyclerOptions<FindFriend> options = new FirestoreRecyclerOptions.Builder<FindFriend>()
//                .setQuery(documentSnapshotTask, FindFriend.class)
//                .build();

        //recyclerAdapter
        adapter = new FirestoreRecyclerAdapter<FindFriend, FindFriendViewHolder>(options) {
            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_friend, parent, false);
                return new FindFriendViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, int position, @NonNull FindFriend model) {
                holder.friendName.setText(model.getName());
                holder.friendBio.setText(model.getBio());

                if (model.getUrl() !=null) {
                    Picasso.get().load(model.getUrl()).into(holder.friendImageView);
                } else {
                    holder.friendImageView.setImageResource(R.mipmap.ic_launcher);
                }
            }
        };
        searchFriendList.setHasFixedSize(true);
        searchFriendList.setLayoutManager(new LinearLayoutManager(this));
        searchFriendList.setAdapter(adapter);
    }

    private class FindFriendViewHolder extends RecyclerView.ViewHolder{

        private ImageView friendImageView;
        private TextView friendName, friendBio;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            friendImageView = itemView.findViewById(R.id.friendImageView);
            friendName = itemView.findViewById(R.id.friendNameTV);
            friendBio = itemView.findViewById(R.id.bioTV);
        }
    }
}