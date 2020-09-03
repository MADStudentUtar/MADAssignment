package utar.edu.mad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

public class Profile extends AppCompatActivity {

    TextView profileName, profileBio, profileBirth, profileFav;
    ImageView profileImage;
    Button chatBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //toolbar
        Toolbar toolbar = findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profileImage = (ImageView) findViewById(R.id.profileImageView);
        profileName = (TextView) findViewById(R.id.NameTV);
        profileBio = (TextView) findViewById(R.id.BioTV);
        profileBirth = (TextView) findViewById(R.id.BirthTV);
        profileFav = (TextView) findViewById(R.id.FavTV);

        chatBtn = (Button) findViewById(R.id.chatBtn);

        Intent intent = getIntent();
        final String UrlP = intent.getStringExtra("Url");
        final String NameP = intent.getStringExtra("Name");
        final String IdP = intent.getStringExtra("Id");

        Picasso.get().load(UrlP).into(profileImage);
        profileName.setText(intent.getStringExtra("Name"));
        profileBio.setText(intent.getStringExtra("Bio"));
        profileBirth.setText(intent.getStringExtra("Birthdate"));
        profileFav.setText(intent.getStringExtra("Favouritesong"));

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(Profile.this, Message.class);
                profileIntent.putExtra("Id", IdP);
                profileIntent.putExtra("Name", NameP);
                profileIntent.putExtra("Url", UrlP);
                startActivityForResult(profileIntent,1);
            }
        });
    }
}