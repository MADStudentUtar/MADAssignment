package utar.edu.mad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Message extends AppCompatActivity {
    ScrollView messagesScrollView;
    LinearLayout messagesWrapper;
    List<LinearLayout> messagesContainer;
    List<ImageView> avatarsImageView;
    List<TextView> messagesTV;
    EditText messageToSend;
    ImageView sendButton;
    Query q;

    // Intent variable
    String friendId;
    String friendName;
    String friendAvatarUrl;

    // Firebase Variable
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference senderCollection;
    CollectionReference receiverCollection;
    DocumentReference senderDocument;
    DocumentReference receiverDocument;
    String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // Get required intent
        friendId = this.getIntent().getStringExtra("Id");
        friendName = this.getIntent().getStringExtra("Name");
        friendAvatarUrl = this.getIntent().getStringExtra("Url");

        // Set friend's name on title
        TextView friendNameView = (TextView) findViewById(R.id.friendNameView);
        friendNameView.setText(friendName);


        // ----- Handle Display Message History Start ----- //
        messagesScrollView = (ScrollView) findViewById(R.id.messagesScrollView);
        messagesWrapper = (LinearLayout) findViewById(R.id.messagesWrapper);

        receiverCollection = db.collection("messages").document(friendId).collection("receivers").document(currentUserID).collection("messageHistory");
        senderCollection = db.collection("messages").document(currentUserID).collection("receivers").document(friendId).collection("messageHistory");
        receiverDocument = db.collection("messages").document(friendId).collection("receivers").document(currentUserID);
        senderDocument = db.collection("messages").document(currentUserID).collection("receivers").document(friendId);
        q = senderCollection.orderBy("timestamp", Query.Direction.DESCENDING);
        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                // Get the messages as a list
                List<DocumentSnapshot> messages = task.getResult().getDocuments();

                displayMessageHistory(messages);
            }
        });
        // ----- Handle Display Message History End ----- //


        // Back button onClickListener
        ImageView backButton = (ImageView) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        // Send Message Button Handler
        sendButton = (ImageView) findViewById(R.id.sendButton);
        messageToSend = (EditText) findViewById(R.id.messageToSend);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageString = messageToSend.getText().toString().trim();
                messageToSend.setText("");

                if(messageString.equals("")) {
                    Toast.makeText(Message.this, "Nothing here", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> messageDocData = new HashMap<>();
                messageDocData.put("message", messageString);
                messageDocData.put("sender", true);
                messageDocData.put("timestamp", new Date());

                senderCollection.add(messageDocData);

                messageDocData.put("sender", false);
                receiverCollection.add(messageDocData);

                Map<String, Object> updateLastMessage = new HashMap<>();
                updateLastMessage.put("lastMessage", messageString);
                updateLastMessage.put("timestamp", new Date());
                senderDocument.set(updateLastMessage, SetOptions.merge());
                receiverDocument.set(updateLastMessage, SetOptions.merge());
            }
        });

        messagesScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                messagesScrollView.getWindowVisibleDisplayFrame(r);
                int screenHeight = messagesScrollView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) {
                    scrollToBottom();
                }
            }
        });

        q.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    System.err.println("Listen failed: " + error);
                    return;
                }

                if (value != null) {
                    displayMessageHistory(value.getDocuments());
                } else {
                    System.out.print("Current data: null");
                }
            }
        });
    }

    private int convertToSP(int px) {
        float scale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (px * scale);
    }

    private void scrollToBottom() {
        messagesScrollView.post(new Runnable() {
            @Override
            public void run() {
                View lastChild = messagesScrollView.getChildAt(messagesScrollView.getChildCount() - 1);
                int bottom = lastChild.getBottom() + messagesScrollView.getPaddingBottom();
                int sy = messagesScrollView.getScrollY();
                int sh = messagesScrollView.getHeight();
                int delta = bottom - (sy + sh);
                messagesScrollView.smoothScrollBy(0, delta);
            }
        });
    }

    private void displayMessageHistory(List<DocumentSnapshot> messages) {
        messagesWrapper.removeAllViews();
        messagesContainer = new ArrayList<LinearLayout>();
        avatarsImageView = new ArrayList<ImageView>();
        messagesTV = new ArrayList<TextView>();

        // Loop the messages list
        for (int i = messages.size() - 1; i >= 0; i--) {
            // Get message and sender boolean
            String message = (String) messages.get(i).get("message");
            Boolean sender = (Boolean) messages.get(i).get("sender");

            int index = messages.size() - 1 - i;

            // Initialize messageContainer LinearLayout
            LinearLayout messageContainer = new LinearLayout(Message.this);
            LinearLayout.LayoutParams messageContainerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            // Set the message into TextView
            TextView messageTV = new TextView(Message.this);
            messageTV.setText(message);
            messageTV.setTextSize(20);
            LinearLayout.LayoutParams messageTVParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            // Initialize avatar ImageView
            ImageView avatar = new ImageView(Message.this);
            avatar.setLayoutParams(new RelativeLayout.LayoutParams(convertToSP(45), convertToSP(45)));
            avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // Setup and display the layout if currentUser is sender
            if (sender) {
                // Load the avatar url into ImageView
                Picasso.get().load("https://i.kym-cdn.com/photos/images/original/001/168/825/826.jpg").into(avatar);

                // Set the attributes for message TextView
                messageTV.setTextColor(Color.BLACK);
                messageTV.setPadding(convertToSP(10), convertToSP(10), convertToSP(25), convertToSP(10));
                messageTV.setBackgroundResource(R.drawable.outgoing_chat);
                messageTVParams.setMargins(convertToSP(80), 0, 0, 0);
                messageTV.setLayoutParams(messageTVParams);

                // Set the attributes for messageContainer LinearLayout
                messageContainerParams.setMargins(0, 0, 0, convertToSP(10));
                messageContainer.setGravity(Gravity.RIGHT);
                messageContainer.setLayoutParams(messageContainerParams);

                // Add the avatar ImageView and message TextView to the messageContainer LinearLayout
                avatarsImageView.add(avatar);
                messagesTV.add(messageTV);
                messageContainer.addView(messagesTV.get(index));
                messageContainer.addView(avatarsImageView.get(index));

            } else {   // Setup and display the layout if currentUser is receiver

                // Load the avatar url into ImageView
                Picasso.get().load(friendAvatarUrl).into(avatar);

                // Set the attributes for message TextView
                messageTV.setTextColor(Color.BLACK);
                messageTV.setPadding(convertToSP(25), convertToSP(10), convertToSP(10), convertToSP(10));
                messageTV.setBackgroundResource(R.drawable.incoming_chat);
                messageTVParams.setMargins(0, 0, convertToSP(80), 0);
                messageTV.setLayoutParams(messageTVParams);

                // Set the attributes for messageContainer LinearLayout
                messageContainerParams.setMargins(0, 0, 0, convertToSP(10));
                messageContainer.setLayoutParams(messageContainerParams);

                // Add the avatar ImageView and message TextView to the messageContainer LinearLayout
                avatarsImageView.add(avatar);
                messagesTV.add(messageTV);
                messageContainer.addView(avatarsImageView.get(index));
                messageContainer.addView(messagesTV.get(index));
            }

            // Add the messageContainer LinearLayout into messagesWrapper LinearLayout
            messagesContainer.add(messageContainer);
            messagesWrapper.addView(messagesContainer.get(index));
        }

        scrollToBottom();
    }
}