package com.pol.happyflight.GamesFragments;

import android.content.ClipData;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pol.happyflight.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlightCrush  extends Fragment {
    FirebaseFirestore db;
    String TAG = "FLIGHTCRASH";
    boolean gameHost;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        db = FirebaseFirestore.getInstance();
        WifiManager manager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        final String address = info.getMacAddress();
        gameHost = false;
        CollectionReference gameStat = db.collection("Flight_crash")
                .document(getActivity().getIntent().getExtras().getString("id")).collection("GameStat");
        gameStat.document("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            gameHost = Arrays.asList(document.get("Users")).get(0).toString().equals(address);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        if (gameHost)initializeGame(gameStat);
        return inflater.inflate(R.layout.flight_crush, container, false);

    }

    private void initializeGame(CollectionReference gameStat) {
        Map<String, Object> data1 = new HashMap<>();
    }
}
