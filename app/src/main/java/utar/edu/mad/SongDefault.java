package utar.edu.mad;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SongDefault {
    DocumentReference documentReference;
    DocumentReference documentReference2;
    DocumentReference documentReference3;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    String currentUserID;

    public void loadSong() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getInstance().getCurrentUser().getUid();
        Map<String, String> Mojito_JayChou = new HashMap<>();
        Mojito_JayChou.put("artist", "Jay Chou");
        Mojito_JayChou.put("karaoke", "https://firebasestorage.googleapis.com/v0/b/karaokie-7aaa8.appspot.com/o/songs%2FMojito-%20Karaoke%20ver.mp3?alt=media&token=e6754594-d5ef-4c7b-a210-ce5b601063c1");
        Mojito_JayChou.put("lyrics", "https://firebasestorage.googleapis.com/v0/b/karaokie-7aaa8.appspot.com/o/lyrics%2FMojito.txt?alt=media&token=834711c9-8399-43a1-b2de-708f38413638");
        Mojito_JayChou.put("song_img", "https://firebasestorage.googleapis.com/v0/b/karaokie-7aaa8.appspot.com/o/songsImg%2FJay%20Chou.jpg?alt=media&token=f9747d8e-068a-4444-96c2-0b7bf5dc4ddf");
        Mojito_JayChou.put("song_title", "Mojito");
        Mojito_JayChou.put("song_url", "https://firebasestorage.googleapis.com/v0/b/karaokie-7aaa8.appspot.com/o/songs%2FMojito.mp3?alt=media&token=ce264bcc-3736-42c2-bc71-fba1df2bdeb4");
        documentReference = db.collection("songs").document(currentUserID).collection("songs_details").document("Mojito-JayChou");
        documentReference.set(Mojito_JayChou);

        Map<String, String> JustTheWayYouAre_BrunoMars = new HashMap<>();
        JustTheWayYouAre_BrunoMars.put("artist", "Bruno Mars");
        JustTheWayYouAre_BrunoMars.put("karaoke", "https://firebasestorage.googleapis.com/v0/b/karaokie-7aaa8.appspot.com/o/songs%2FJust%20the%20Way%20You%20Are%20-%20Karaoke%20ver.mp3?alt=media&token=f480e4ac-93ee-4347-8c2e-4d675df889c5");
        JustTheWayYouAre_BrunoMars.put("lyrics", "https://firebasestorage.googleapis.com/v0/b/karaokie-7aaa8.appspot.com/o/lyrics%2FJust%20The%20Way%20You%20Are.txt?alt=media&token=becfc794-7a00-47bf-ae78-59b522ab65fe");
        JustTheWayYouAre_BrunoMars.put("song_img", "https://firebasestorage.googleapis.com/v0/b/karaokie-7aaa8.appspot.com/o/songsImg%2FBruno%20Mars.jpg?alt=media&token=3a0a3ee2-8e57-4725-a842-33c7a8a8bed7");
        JustTheWayYouAre_BrunoMars.put("song_title", "Just The Way You Are");
        JustTheWayYouAre_BrunoMars.put("song_url", "https://firebasestorage.googleapis.com/v0/b/karaokie-7aaa8.appspot.com/o/songs%2FJust%20the%20Way%20You%20Are.mp3?alt=media&token=68b243a4-d91d-4869-a115-57b32d869bfb");
        documentReference2 = db.collection("songs").document(currentUserID).collection("songs_details").document("Just The Way You Are-Bruno Mars");
        documentReference2.set(JustTheWayYouAre_BrunoMars);

        Map<String, String> HowYouLikeThat_BlackPink = new HashMap<>();
        HowYouLikeThat_BlackPink.put("artist", "BlackPink");
        HowYouLikeThat_BlackPink.put("karaoke", "https://firebasestorage.googleapis.com/v0/b/karaokie-7aaa8.appspot.com/o/songs%2FHow%20You%20Like%20That%20-Karaoke%20ver.mp3?alt=media&token=cf444450-9199-4a9e-a629-4334e4e52f6d");
        HowYouLikeThat_BlackPink.put("lyrics", "https://firebasestorage.googleapis.com/v0/b/karaokie-7aaa8.appspot.com/o/lyrics%2FHow%20You%20Like%20That.txt?alt=media&token=c2b18528-627c-46d4-9fe5-1136a60e3cba");
        HowYouLikeThat_BlackPink.put("song_img", "https://firebasestorage.googleapis.com/v0/b/karaokie-7aaa8.appspot.com/o/songsImg%2FBlackPink.jpg?alt=media&token=65b15be9-697f-4cb5-ab7b-7c011e9c90b3");
        HowYouLikeThat_BlackPink.put("song_title", "How You Like That");
        HowYouLikeThat_BlackPink.put("song_url", "https://firebasestorage.googleapis.com/v0/b/karaokie-7aaa8.appspot.com/o/songs%2FHow%20You%20Like%20That.mp3?alt=media&token=ec06adfb-fbe7-434a-bd61-476f5b329cd6");
        documentReference3 = db.collection("songs").document(currentUserID).collection("songs_details").document("How You Like That-BlackPink");
        documentReference3.set(HowYouLikeThat_BlackPink);
    }
}
