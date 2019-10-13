package com.pol.happyflight.GamesFragments;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pol.happyflight.GameRoom;
import com.pol.happyflight.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomStatus extends Fragment {
    private static final String TAG = "ROOMSTATUS";
    TextView maxJugTxt, minJugTxt, NumJugTxt;
    FirebaseFirestore db;
    View view;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.room_status, container, false);
        init();
        incrementUser();
        addUser();
        return view;
    }
    private void init(){


        Log.d(TAG,"max:" +  GameRoom.maxJug);
        maxJugTxt = view.findViewById(R.id.maxPlayers);
        minJugTxt = view.findViewById(R.id.minPlayers);
        NumJugTxt = view.findViewById(R.id.numJug);


        db = FirebaseFirestore.getInstance();
    }
    private void updateCounters(){
        maxJugTxt.setText(""+GameRoom.maxJug);
        minJugTxt.setText(""+GameRoom.minJug);
        NumJugTxt.setText(""+(GameRoom.numJug));
    }
    private void addUser(){
       List<String> arr = new ArrayList() ;
        arr.add(GameRoom.mac);

        /*
        db.collection(GameRoom.id).document(GameRoom.roomId).collection("GameStat").document("Users")
                .update("UserID", arr);
*/
        db.collection(GameRoom.id).document(GameRoom.roomId).collection("GameStat")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                List<String> arr = (List<String>) document.get("UserID");
                                Map<String, String> status = new HashMap    <String, String>() {
                                };
                                if(arr==null){arr = new ArrayList<>(); }
                                arr.add(GameRoom.mac);
                                db.collection(GameRoom.id).document(GameRoom.roomId).collection("GameStat").document("Users")
                                        .update("UserID", arr);
                                db.collection(GameRoom.id).document(GameRoom.roomId).collection("GameStat").document("Status")
                                        .set(status);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }

    private void checkForUpdates(){
        final DocumentReference docRef = db.collection(GameRoom.id).document(GameRoom.roomId);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {

                    GameRoom.numJug = Integer.parseInt(snapshot.get("Num jug").toString());
                    updateCounters();
                    Log.d(TAG,GameRoom.numJug + ">="+GameRoom.minJug);

                    if(GameRoom.numJug>=GameRoom.minJug){
                        Log.d(TAG,"rtu");
                        loadGame();

                    }else{
                        Log.d(TAG,"false");
                    }

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }
    private void loadGame(){
        Log.d(TAG, GameRoom.id);
        db.collection(GameRoom.id).document(GameRoom.roomId)
                .update("En curs", true);
        switch(GameRoom.id){
            case "Flight_crash":
                getFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new FlightCrush()).commit();
                break;
            case "Passengers":
                getFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new Passengers()).commit();
                break;
            case "Trivia":
                getFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new Trivia()).commit();
                break;
        }
    }
    private void incrementUser(){
        Log.d(TAG, "Num jug1 " + GameRoom.numJug);
        GameRoom.numJug++;
        Log.d(TAG, "Num jug2 " + GameRoom.numJug);
        db.collection(GameRoom.id).document(GameRoom.roomId)
                .update("Num jug", GameRoom.numJug)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        updateCounters();
                        checkForUpdates();
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


}
