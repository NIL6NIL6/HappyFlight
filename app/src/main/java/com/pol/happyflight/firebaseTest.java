package com.pol.happyflight;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class firebaseTest extends AppCompatActivity {
    TextView txtX, txtY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String TAG = "TEST";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_test);

        txtX = findViewById(R.id.txtX);
        txtY = findViewById(R.id.txtY);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("test").document("DH3DGQXSszt5i7GN7pYC");
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
                    txtX.setText(""+snapshot.get("x"));
                    txtY.setText(""+snapshot.get("y"));

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

    }
}
