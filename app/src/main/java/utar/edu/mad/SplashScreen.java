package utar.edu.mad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreen extends AppCompatActivity {

    private static int timer = 5000;

    Animation topAnim, bottomAnim;
    ImageView logo;
    TextView name;


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences profileSP;
    SharedPreferences.Editor profileEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        profileSP = getSharedPreferences("profile", MODE_PRIVATE);
        profileEditor = profileSP.edit();

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        logo = findViewById(R.id.logo);
        name = findViewById(R.id.name);

        logo.setAnimation(topAnim);
        name.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser == null)
                {
                    Intent intent = new Intent(SplashScreen.this,login.class);
                    startActivity(intent);
                    finish();
                } else {
                    String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    db.collection("user").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult().exists()){

                                String name_result = task.getResult().getString("name");
                                String bio_result = task.getResult().getString("bio");
                                String birthdate_result = task.getResult().getString("birthdate");
                                String favouritesong_result = task.getResult().getString("favouritesong");
                                String Url = task.getResult().getString("url");

                                profileEditor.putString("name", name_result);
                                profileEditor.putString("bio", bio_result);
                                profileEditor.putString("birthdate", birthdate_result);
                                profileEditor.putString("favouritesong", favouritesong_result);
                                profileEditor.putString("url", Url);
                                profileEditor.commit();

                                Intent intent = new Intent(SplashScreen.this, SongList.class);
                                startActivity(intent);
                                finish();
                            } else{
                                Intent intent = new Intent(SplashScreen.this, ShowProfile.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        }, timer);
    }
}