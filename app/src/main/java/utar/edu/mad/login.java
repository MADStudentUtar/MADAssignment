package utar.edu.mad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class login extends AppCompatActivity {
    private EditText loginEmailtext;
    private EditText loginPasswordtext;
    private Button loginbtn;
    private Button loginregisterbtn;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private CheckBox checkBox;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences profileSP;
    SharedPreferences.Editor profileEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        profileSP = getSharedPreferences("profile", MODE_PRIVATE);
        profileEditor = profileSP.edit();

        mAuth = FirebaseAuth.getInstance();
         loginEmailtext = findViewById(R.id.login_email);
         loginPasswordtext = findViewById(R.id.login_password);
         loginbtn = findViewById(R.id.login_button);
         loginregisterbtn = findViewById(R.id.login_register_button);
         progressBar = findViewById(R.id.login_progressbar);
         checkBox = findViewById(R.id.login_checkbox);


         loginbtn.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View view) {
                 String loginEmail = loginEmailtext.getText().toString();
                 String loginpass = loginPasswordtext.getText().toString();
                  if (!TextUtils.isEmpty(loginEmail) || !TextUtils.isEmpty(loginpass)){
                      progressBar.setVisibility(View.VISIBLE);

                      mAuth.signInWithEmailAndPassword(loginEmail,loginpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                          @Override
                          public void onComplete(@NonNull Task<AuthResult> task) {
                              if (task.isSuccessful()){
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

                                              sendtoMain();
                                          } else{
                                              Intent intent = new Intent(login.this, ShowProfile.class);
                                              startActivity(intent);
                                          }
                                      }
                                  });
                              }else{
                                  String error = task.getException().getMessage();
                                  Toast.makeText(getApplicationContext(),"Error :" + error,Toast.LENGTH_LONG).show();
                              }
                          }
                      });

                  }
             }
         });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    loginPasswordtext.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    loginPasswordtext.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
         loginregisterbtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent(login.this,Register.class);
                 startActivity(intent);
                 finish();
             }
         });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){
            Intent intent = new Intent(login.this,SongList.class);
            startActivity(intent);
            finish();
        }
    }

    private void sendtoMain(){
        Intent intent = new Intent(login.this,SongList.class);
        startActivity(intent);
        finish();
    }
}