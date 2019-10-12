package com.pol.happyflight.GamesFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pol.happyflight.Classes.Constants;
import com.pol.happyflight.Classes.Room;
import com.pol.happyflight.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Passengers  extends Fragment {
    String id = "Passengers";
    FirebaseFirestore db;
    String TAG = "PASSENGERS";
    int random = new Random().nextInt(10000000);

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        db = FirebaseFirestore.getInstance();
        searchForRoom();
        return inflater.inflate(R.layout.passengers, container, false);


    }
    private String searchForRoom(){
        Log.d(TAG, "LEGO");
        db.collection(id)
                .whereEqualTo("En curs", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getId();
                                boolean enCurs =  Boolean.valueOf(document.get("En curs").toString());
                                int maxJug = Integer.parseInt(document.get("Max jug").toString());
                                int minJug = Integer.parseInt(document.get("Min jug").toString());
                                int numJug = Integer.parseInt(document.get("Num jug").toString());
                                if(numJug == maxJug) continue;
                                Room room = new Room(id,enCurs ,maxJug ,minJug ,numJug+1);
                                joinRoom(room);
                                break;
                            }
                            Map<String, Object> roomFC = new HashMap<>();
                            roomFC.put("En curs",true);
                            roomFC.put("Max jug",4);
                            roomFC.put("Min jug",2);
                            roomFC.put("Num jug",1);

                            db.collection(id).document(getString(random))
                                    .set(roomFC)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document", e);
                                        }
                                    });

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return "";
    }
    private void joinRoom(Room room){
        Log.d(TAG, "JOINED ROOM " + room.getId());
    }
}