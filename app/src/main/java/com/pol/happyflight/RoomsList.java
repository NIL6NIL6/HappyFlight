package com.pol.happyflight;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pol.happyflight.Classes.RoomContainer;
import com.pol.happyflight.Classes.Room;
import com.pol.happyflight.Classes.RoomContainerAdapter;
import com.pol.happyflight.GamesFragments.FlightCrush;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class RoomsList extends AppCompatActivity {
    LinearLayout gamesList;
    RecyclerView recycleView;
    public static Context context;
    private RoomContainerAdapter mAdapter;
    String TAG = "ROOMS";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms_list);
        gamesList = findViewById(R.id.gamesList);
        recycleView = findViewById(R.id.recycle_view);
        context = RoomsList.this;

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
                            List<RoomContainer> RoomContainerList = new ArrayList<>();
                            int[] d = {R.drawable.flight_crash, R.drawable.passengers, R.drawable.trivia};
                            int index = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.get("name").toString();
                                String id = document.get("id").toString();
                                /*
                                Button but = new Button(RoomsList.this);
                                but.setText(name);
                                but.setTag(id);
                                but.setOnClickListener(gameClickEvent);
                                gamesList.addView(but);
                                */
                                RoomContainerList.add(new RoomContainer(name, getResources().getDrawable( d[index++]), id));
                            }
                            mAdapter = new RoomContainerAdapter(RoomContainerList);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            recycleView.setLayoutManager(mLayoutManager);
                            recycleView.setItemAnimator(new DefaultItemAnimator());
                            recycleView.setAdapter(mAdapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
    public static void enterRoom(String name, String id){

        String mac = GameRoom.getMacAddr();
        Intent intent = new Intent(context, GameRoom.class);
        intent.putExtra("name", name);
        intent.putExtra("id", id);
        intent.putExtra("mac", mac);
        context.startActivity(intent);
    }
    View.OnClickListener gameClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = manager.getConnectionInfo();
            String mac = info.getMacAddress();
            Intent intent = new Intent(RoomsList.this, GameRoom.class);
            Button b = (Button) v;
            String name = b.getText().toString();
            String id = b.getTag().toString();
            intent.putExtra("name", name);
            intent.putExtra("id", id);
            intent.putExtra("mac", mac);
            startActivity(intent);
        }
    };
}
