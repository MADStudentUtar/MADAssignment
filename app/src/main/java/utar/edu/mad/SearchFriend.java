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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;;

public class SearchFriend extends AppCompatActivity {

    private ImageButton searchFriendButton;
    private EditText searchFriendText;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference friendRef = db.collection("user");
    private RecyclerView searchFriendList;

    private  FirestoreRecyclerAdapter adapter;

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
//        Query query = friendRef.orderBy("name", Query.Direction.ASCENDING);

        //recyclerOption
        FirestoreRecyclerOptions<FindFriend> options = new FirestoreRecyclerOptions.Builder<FindFriend>()
                .setQuery(query, FindFriend.class)
                .build();

        //recyclerAdapter
        adapter = new FirestoreRecyclerAdapter<FindFriend, SearchFriend.SearchFriendViewHolder>(options) {
            @NonNull
            @Override
            public SearchFriend.SearchFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_friend, parent, false);
                return new SearchFriend.SearchFriendViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull SearchFriend.SearchFriendViewHolder holder, int position, @NonNull FindFriend model) {
                holder.friendName.setText(model.getName());
                holder.friendBio.setText(model.getBio());
                holder.friendImageView.setImageResource(R.mipmap.ic_launcher);
            }
        };
        searchFriendList.setAdapter(adapter);
    }

    public static class SearchFriendViewHolder extends RecyclerView.ViewHolder{

        private ImageView friendImageView;
        private TextView friendName, friendBio;

        public SearchFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            friendImageView = itemView.findViewById(R.id.friendImageView);
            friendName = itemView.findViewById(R.id.friendNameTV);
            friendBio = itemView.findViewById(R.id.friendBioTV);
        }
    }
}