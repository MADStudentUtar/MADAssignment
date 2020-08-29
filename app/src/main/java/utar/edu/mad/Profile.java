package utar.edu.mad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.InputStream;

public class Profile extends AppCompatActivity {
    ImageView profilePicIV;
    ImageView profileCoverIV;
    TextView usernameTV;
    TextView bioTV;
    TextView birthDateTV;
    TextView favouriteSongTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePicIV = (ImageView) findViewById(R.id.profile_pic);
        profileCoverIV = (ImageView) findViewById(R.id.profile_cover);
        usernameTV = (TextView) findViewById(R.id.usernameTV);
        bioTV = (TextView) findViewById(R.id.bioTV);
        birthDateTV = (TextView) findViewById(R.id.birthDateTV);
        favouriteSongTV = (TextView) findViewById(R.id.favouriteSongTV);

        //Get user detail from SharedPreferences
        SharedPreferences userDetail = getSharedPreferences("UserDetail", MODE_PRIVATE);
        String username = userDetail.getString("username", "User");
        String bio = userDetail.getString("bio", "Let's sing!");
        String birthDate = userDetail.getString("birthDate", " - ");
        String favouriteSong = userDetail.getString("favouriteSong", " - ");
        String profilePicURL = "https://www.pngitem.com/pimgs/m/146-1468479_my-profile-icon-blank-profile-picture-circle-hd.png";
        String profileCoverURL = "https://external-preview.redd.it/Rc0lOSuC2xM8I8J5-ZdL1oJBIKId6HgKXuVeFQ_PVrU.jpg?auto=webp&s=c7f1a680c7a69dfcd86fb268d9531d34a52e07e2";


        profilePicIV.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams params =  profilePicIV.getLayoutParams();
                params.width = profilePicIV.getHeight();
                profilePicIV.setLayoutParams(params);
                profilePicIV.postInvalidate();
            }
        });


        //Load profile pic and display using URL
        LoadImage loadProfilePic = new LoadImage(profilePicIV);
        loadProfilePic.execute(profilePicURL);

        //Load profile cover and display using URL
        LoadImage loadProfileCover = new LoadImage(profileCoverIV);
        loadProfileCover.execute(profileCoverURL);

        //Display user details
        usernameTV.setText(username);
        bioTV.setText("My name is Chai Wan Xin, Let's sing! I am a programmer with no life by the way. what are your interest? Let me know! I am happy to know new friends");
        birthDateTV.setText(birthDate);
        favouriteSongTV.setText(favouriteSong);


        //Setting Button OnClickListener
        FloatingActionButton settingButton = (FloatingActionButton) findViewById(R.id.settingButton);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Profile.this, MainActivity.class));
            }
        });



        //Bottom navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        //Set home page selected
        bottomNavigationView.setSelectedItemId(R.id.profile);

        //Perform ItemSelectedListener in Navigation Bar
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.songList:
                        startActivity(new Intent(Profile.this, SongList.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.friendList:
//                        startActivity(new Intent(getApplicationContext(), ));
//                        overridePendingTransition(0,0);
//                        return true;
                    case R.id.chat:
//                        startActivity(new Intent(getApplicationContext(), ));
//                        overridePendingTransition(0,0);
//                        return true;
                    case R.id.profile:
                        return true;
                }
                return false;
            }
        });
    }

    //Load the image using URL
    private class LoadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public LoadImage(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bitmap = null;

            try {
                InputStream in = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
}