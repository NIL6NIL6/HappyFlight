package com.pol.happyflight;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class RoomsList extends AppCompatActivity {
    LinearLayout gamesList;
    String TAG = "ROOMS";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms_list);
        gamesList = findViewById(R.id.gamesList);

    }
    @Override
    protected void onResume() {
        super.onResume();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("games")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            gamesList.removeAllViews();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.get("name").toString();
                                Button but = new Button(RoomsList.this);
                                but.setText(name);
                                but.setOnClickListener(gameClickEvent);
                                gamesList.addView(but);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    View.OnClickListener gameClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Button b = (Button) v;
            String name = b.getText().toString();
            Log.d(TAG, name);
            switch(name){
                case "Tetris":

                    break;
                case "Parchís":

                    break;
                case "Avions":

                    break;
            }
        }
    };
}
