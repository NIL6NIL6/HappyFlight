package com.pol.happyflight;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pol.happyflight.GamesFragments.FlightCrush;
import com.pol.happyflight.GamesFragments.Passengers;
import com.pol.happyflight.GamesFragments.RoomStatus;
import com.pol.happyflight.GamesFragments.Trivia;

import static java.lang.Thread.sleep;

public class GameRoom extends AppCompatActivity {
    public static String name;
    public  static String id;
    public  static String mac;
    public static String roomId;
    public static int maxJug;
    public static int minJug;
    public static int numJug;
    String TAG = "GAMEROOM";
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_room);

        init();
        checkForRooms();
        //loadGame(id);
    }

    private void init(){
        Bundle extras = getIntent().getExtras();
        GameRoom.name = extras.getString("name");
        GameRoom.id = extras.getString("id");
        GameRoom.mac = extras.getString("mac");
        db = FirebaseFirestore.getInstance();
    }

    private void checkForRooms(){
        db.collection(id)
                .whereEqualTo("En curs", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                maxJug = Integer.parseInt(document.get("Max jug").toString());
                                minJug = Integer.parseInt(document.get("Max jug").toString());
                                numJug = Integer.parseInt(document.get("Num jug").toString());

                                if(numJug<maxJug){
                                    loadStatusRoom();
                                    GameRoom.roomId = document.getId();
                                    Log.d(TAG, "Welcome to room " + document.getId());
                                    break;
                                }

                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
    private void loadStatusRoom(){
        Log.w(TAG, "enter");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = new RoomStatus();

        Bundle bundle = new Bundle();
        bundle.putInt("maxJug", maxJug);
        bundle.putInt("minJug", minJug);
        bundle.putInt("numJung", numJug);

        fragment.setArguments(bundle);
        fragmentTransaction.add(R.id.fragmentLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Num jug1 " + numJug);
        numJug--;
        Log.d(TAG, "Num jug2 " + numJug);
        db.collection(GameRoom.id).document(GameRoom.roomId)
                .update("Num jug", numJug)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        Log.w(TAG, "cool");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }



    private void loadGame(String id){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        /*
        Log.d(TAG, id);
        switch(id){
            case "Passengers":
                fragment = new Passengers();
                break;
            case "Trivia":
                fragment = new Trivia();
                break;
            case "Flight_crash":
                fragment = new FlightCrush();

                break;
        }

        fragmentTransaction.add(R.id.fragmentLayout, fragment);
        fragmentTransaction.commit();
        */
    }
}
