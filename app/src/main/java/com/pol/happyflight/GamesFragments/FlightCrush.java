package com.pol.happyflight.GamesFragments;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.pol.happyflight.GameRoom;
import com.pol.happyflight.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FlightCrush  extends Fragment {
    FirebaseFirestore db;
    String TAG = "FLIGHTCRASH";
    boolean gameHost = false;
    List<Object> users;
    View view;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.flight_crush, container, false);
        db = FirebaseFirestore.getInstance();
        WifiManager manager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        final String address = info.getMacAddress();
        final CollectionReference gameStat = db.collection("Flight_crash")
                .document(GameRoom.roomId).collection("GameStat");
        gameStat.document("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            gameHost = ((ArrayList) document.get("UserID")).get(0).toString().equals(address);
                            if (gameHost)initializeGame(gameStat);
                            Log.w(TAG, "Hosted: " + gameHost, task.getException());
                            Log.w(TAG, address, task.getException());
                            Log.w(TAG, ((ArrayList) document.get("UserID")).get(0).toString(), task.getException());
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        View v = inflater.inflate(R.layout.flight_crush, container, false);
        defineButtonClicks(v);
        final DocumentReference docRef = gameStat.document("Status");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    if ((Boolean) ((Map)snapshot.get("Deceased")).get(address)){
                        if (!gameHost) getActivity().onBackPressed();
                    }
                    Log.w(TAG, "Alive");
                    if (gameHost){
                        Map current = (Map) snapshot.get("Current");
                        Map destination = (Map) snapshot.get("Destination");
                        Log.w(TAG, "Mapped");
                        if (current.keySet().size() == destination.keySet().size()){
                            //HashMap<String,Float> intersections = theAlgorithm(current,destination);
                            //Primer borrem els destination aixi evitem entrar mes cops en aquesta part(per si es crida al listener cada cop que fem un canvi)
                            Map copy = destination;
                            HashMap<String,Object> dummy = new HashMap<>();
                            dummy.put("Destination",new HashMap<>());
                            Log.w(TAG, "Pre-update");
                            docRef.set(dummy, SetOptions.merge());
                            Log.w(TAG, "Post-update");
                            HashMap<String,Boolean> deceased =  (HashMap<String,Boolean>) snapshot.get("Deceased");
                            /*for (String s : intersections.keySet()){
                                deceased.put(s,true);
                            }*/
                            dummy = new HashMap<>();
                            dummy.put("Deceased",deceased);
                            docRef.set(dummy,SetOptions.merge());
                            HashMap<String,Integer> newCurrent = new HashMap<>();
                            //Fer per aqui les animacions de rip
                            /*for (Object o : current.keySet()){
                                if (intersections.containsKey(o.toString()))continue;
                                newCurrent.put(o.toString(),(Integer)current.get(o));
                            }
                            dummy = new HashMap<>();
                            dummy.put("Current",newCurrent);
                            dummy.put("Ready", false);*/
                            docRef.set(dummy,SetOptions.merge());
                        }
                    }
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
        return v;
    }

    private void initializeGame(final CollectionReference gameStat) {
        gameHost = true;
        gameStat.document("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Map<String, Object> data = new HashMap<>();
                            DocumentSnapshot document = task.getResult();
                            users = ((ArrayList) document.get("UserID"));
                            //Hardcoded quantity of buttons
                            int nBut = 10;
                            HashMap<String,ArrayList<Boolean>> visited = new HashMap<>();
                            HashMap<String,Boolean> Deceased = new HashMap<>();
                            for (Object user : users){
                                ArrayList<Boolean> aux = new ArrayList<>();
                                for (int i = 0; i<nBut; ++i){
                                    aux.add(false);
                                }
                                visited.put(user.toString(),aux);
                                Deceased.put(user.toString(),false);
                            }
                            data.put("Destination",new HashMap<String,Integer>());
                            data.put("Deceased",Deceased);
                            HashMap<String,Integer> current = new HashMap<>();
                            ArrayList<Boolean> used = new ArrayList<>();
                            for (int i = 0; i<nBut; ++i){
                                used.add(false);
                            }
                            Random random = new Random();
                            int num;
                            for (Object user : users){
                                while(used.get((num = random.nextInt(nBut)))){}
                                used.set(num,true);
                                current.put(user.toString(),num);
                                visited.get(user.toString()).set(num,true);
                            }
                            data.put("UserVisited",visited);
                            data.put("Current",current);
                            placeInfo(gameStat,data);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }

    private void placeInfo(CollectionReference gameStat, Map<String, Object> data) {
        gameStat.document("Status")
                .set(data)
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
    }

    private void defineButtonClicks(View v) {
        for (int i = 0; i < 10; i++) {
            String buttonID = "destination".concat(Integer.toString(i+1));
            int resID = getResources().getIdentifier(buttonID, "id", "com.pol.happyflight");
            Button button = (Button)v.findViewById(resID);
            final int idNum = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onButtonClick(view, idNum);
                }
            });
        }
    }

    private void onButtonClick(View view, int i) {
        if (isPositionAvailable(i))
            goToPosition(i);
        else
            incorrectPosition(view);
    }

    private boolean isPositionAvailable(int i) {
        ArrayList<Boolean> positionsAvailability = getPositionsAvailability();
        return !(positionsAvailability.get(i));
    }

    private void incorrectPosition(View view) {

    }

    private void goToPosition(int pos) {
        //FIREBASE CODE HERE
    }

    private int getCurrentPosition() {
        int pos = 0;

        //FIREBASE CODE HERE

        return pos;
    }

    private ArrayList<Boolean> getPositionsAvailability() {
        ArrayList<Boolean> positionsAvailability = new ArrayList<Boolean>();

        //FIREBASE CODE HERE

        return positionsAvailability;
    }
}
