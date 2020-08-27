package utar.edu.mad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Chat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

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
//                        startActivity(new Intent(getApplicationContext(), ));
//                        overridePendingTransition(0,0);
//                        return true;
                    case R.id.friendList:
//                        startActivity(new Intent(getApplicationContext(), ));
//                        overridePendingTransition(0,0);
//                        return true;
                    case R.id.profile:
//                        startActivity(new Intent(getApplicationContext(), ));
//                        overridePendingTransition(0,0);
//                        return true;
                }
                return false;
            }
        });
    }
}