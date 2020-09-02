package utar.edu.mad;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Chat extends AppCompatActivity {
    LinearLayout chatsWrapper;
    List<LinearLayout> chatContainers;
    List<ImageView> receiverAvatars;
    List<LinearLayout> chatInfoContainers;
    List<TextView> receiverNames;
    List<TextView> lastMessages;

    // Firebase variable
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference receiversCollection;
    String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatsWrapper = (LinearLayout) findViewById(R.id.chatsWrapper);

        // Get chats from firebase
        receiversCollection = db.collection("messages").document(currentUserID).collection("receivers");
        receiversCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> chats = task.getResult().getDocuments();

                displayChats(chats);
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

    private void displayChats(List<DocumentSnapshot> chats) {
        chatsWrapper.removeAllViews();
        chatContainers = new ArrayList<LinearLayout>();
        receiverAvatars = new ArrayList<ImageView>();
        chatInfoContainers = new ArrayList<LinearLayout>();
        receiverNames = new ArrayList<TextView>();
        lastMessages = new ArrayList<TextView>();

        System.out.println(chats.size());

        // Loop the chats
        for(int i = 0; i < chats.size(); i++) {
            final String receiverId = chats.get(i).getId();
            final String lastMessageString = (String) chats.get(i).get("lastMessage");
            final int index = i;

            db.collection("user").document(receiverId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    final String name = (String) task.getResult().get("name");
                    final String url = (String) task.getResult().get("url");

                    // Declare all the views required
                    LinearLayout chatContainer = new LinearLayout(Chat.this);
                    ImageView receiverAvatar = new ImageView(Chat.this);
                    LinearLayout chatInfoContainer = new LinearLayout(Chat.this);
                    TextView receiverName = new TextView(Chat.this);
                    TextView lastMessage = new TextView(Chat.this);


                    // chatContainer LinearLayout set attributes
                    chatContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    chatContainer.setBackgroundResource(R.drawable.border_bottom);
                    chatContainer.setPadding(convertToSP(10), convertToSP(10), convertToSP(10), convertToSP(10));


                    // receiverAvatar ImageView set attributes
                    Picasso.get().load(url).into(receiverAvatar);
                    LinearLayout.LayoutParams avatarImageViewParams = new LinearLayout.LayoutParams(convertToSP(60), convertToSP(60));
                    avatarImageViewParams.setMargins(0, 0, convertToSP(10), 0);
                    receiverAvatar.setLayoutParams(avatarImageViewParams);
                    receiverAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);


                    // chatInfoContainer LinearLayout set attributes
                    chatInfoContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                    chatInfoContainer.setOrientation(LinearLayout.VERTICAL);


                    // receiverName TextView set attributes
                    LinearLayout.LayoutParams nameTextViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    nameTextViewParams.setMargins(0, 0, 0, convertToSP(5));
                    receiverName.setText(name);
                    receiverName.setTextSize(20);
                    receiverName.setTypeface(null, Typeface.BOLD);
                    receiverName.setLayoutParams(nameTextViewParams);


                    // lastMessage TextView set attributes
                    lastMessage.setText(lastMessageString);
                    lastMessage.setTextSize(16);
                    lastMessage.setMaxLines(1);
                    lastMessage.setEllipsize(TextUtils.TruncateAt.END);


                    // Add the views to the List
                    chatContainers.add(chatContainer);
                    receiverAvatars.add(receiverAvatar);
                    chatInfoContainers.add(chatInfoContainer);
                    receiverNames.add(receiverName);
                    lastMessages.add(lastMessage);


                    // Display all the views
                    chatInfoContainers.get(index).addView(receiverNames.get(index));
                    chatInfoContainers.get(index).addView(lastMessages.get(index));
                    chatContainers.get(index).addView(receiverAvatars.get(index));
                    chatContainers.get(index).addView(chatInfoContainers.get(index));
                    chatsWrapper.addView(chatContainers.get(index));

                    chatInfoContainers.get(index).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Chat.this, Message.class);
                            intent.putExtra("Id", receiverId);
                            intent.putExtra("Name", name);
                            intent.putExtra("Url", url);
                            startActivity(intent);
                        }
                    });
                }
            });
        }
    }

    private int convertToSP(int px) {
        float scale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (px * scale);
    }
}