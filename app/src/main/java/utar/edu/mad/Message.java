package utar.edu.mad;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Message extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // Get required intent
        String friendName = this.getIntent().getStringExtra("friendName");

        // Set friend's name on title
        TextView friendNameView = (TextView) findViewById(R.id.friendNameView);
        friendNameView.setText(friendName);


        // Back button onClickListener
        ImageView backButton = (ImageView) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}