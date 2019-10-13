package com.pol.happyflight.GamesFragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.media.Image;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FlightCrush  extends Fragment {
    FirebaseFirestore db;
    String TAG = "FLIGHTCRASH";
    boolean gameHost = false;
    List<Object> users;
    View view;
    String address;
    ImageView[] buttons;
    ImageView plane;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.flight_crush, container, false);
        db = FirebaseFirestore.getInstance();

        address = GameRoom.getMacAddr();
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
        final DocumentReference docRef = gameStat.document("Status");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    if (snapshot.get("Deceased") == null) return;
                    if ((Boolean) ((Map)snapshot.get("Deceased")).get(address)){
                        if (!gameHost) getActivity().onBackPressed();
                    }
                    Log.w(TAG, "Alive");
                    if (gameHost){
                        Map current = (Map) snapshot.get("Current");
                        Map destination = (Map) snapshot.get("Destination");
                        HashMap<String,Object> dummy = new HashMap<>();
                        Log.w(TAG, "Mapped");
                        if (current.keySet().size() == destination.keySet().size()){
                            HashMap<String,Float> intersections = new HashMap<>();//theAlgorithm(current,destination);
                            //Primer borrem els destination aixi evitem entrar mes cops en aquesta part(per si es crida al listener cada cop que fem un canvi)
                            Map copy = destination;
                            dummy.put("Ready",false);
                            dummy.put("Destination",new HashMap<>());
                            Log.w(TAG, "Pre-update");
                            docRef.set(dummy, SetOptions.merge());
                            Log.w(TAG, "Post-update");
                            HashMap<String,Boolean> deceased =  (HashMap<String,Boolean>) snapshot.get("Deceased");
                            for (String s : intersections.keySet()){
                                deceased.put(s,true);
                            }
                            dummy = new HashMap<>();
                            dummy.put("Deceased",deceased);
                            Log.w(TAG, "Calcul RIP");
                            docRef.set(dummy,SetOptions.merge());
                            HashMap<String,Long> newCurrent = new HashMap<>();
                            //Fer per aqui les animacions de rip
                            for (Object o : current.keySet()){
                                if (intersections.containsKey(o.toString()))continue;
                                newCurrent.put(o.toString(),(Long)copy.get(o));
                            }
                            HashMap<String,ArrayList<Boolean>> newVisited = new HashMap<>();
                            HashMap<String,ArrayList<Boolean>> visited = (HashMap<String,ArrayList<Boolean>>) snapshot.get("UserVisited");
                            for (String s : newCurrent.keySet()){
                                ArrayList<Boolean> aux = visited.get(s);
                                Log.w(TAG, s + Math.toIntExact(newCurrent.get(s)));
                                aux.set(Math.toIntExact(newCurrent.get(s)),true);
                                newVisited.put(s,aux);
                            }
                            dummy = new HashMap<>();
                            dummy.put("Current",newCurrent);
                            dummy.put("UserVisited",newVisited);
                            dummy.put("Ready", true);
                            docRef.set(dummy,SetOptions.merge());
                            Log.w(TAG, "Fin");
                        }
                    }
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

        Log.d(TAG, "3");
        return view;
    }

    private void initializeGame(final CollectionReference gameStat) {
        gameHost = true;
        Log.w(TAG, "Ini G");
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
                            data.put("Ready",true);
                            placeInfo(gameStat,data);
                            defineButtonClicks(view);

                            /*Plane init stuff*/
                            int idVisited = current.get(GameRoom.getMacAddr());
                            ImageView a = new ImageView(view.getContext());
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
                            a.setLayoutParams(layoutParams);
                            a.setImageResource(R.drawable.host_plane);
                            ConstraintLayout background = view.findViewById(R.id.map);
                            a.setTranslationY(buttons[idVisited].getTop()+25);
                            a.setTranslationX(buttons[idVisited].getLeft()+25);
                            background.addView(a);
                            plane = a;
                            buttons[idVisited].setAlpha(128);
                            for (int i = 0; i < users.size(); i++) {
                                String userID = (String) users.get(i);
                                if (address != userID) {
                                    ImageView planeImg = new ImageView(view.getContext());
                                    layoutParams = new LinearLayout.LayoutParams(150,150);
                                    planeImg.setLayoutParams(layoutParams);
                                    planeImg.setImageResource(R.drawable.competitor_plane);
                                    background = view.findViewById(R.id.map);
                                    planeImg.setTranslationY(buttons[current.get(userID)].getTop() + 25);
                                    planeImg.setTranslationX(buttons[current.get(userID)].getLeft() + 25);
                                    background.addView(planeImg);
                                }
                            }

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        Toast.makeText(view.getContext(), "", Toast.LENGTH_SHORT).show();
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
        buttons = new ImageView[10];
        for (int i = 0; i < 10; i++) {
            String buttonID = "destination".concat(Integer.toString(i+1));
            int resID = getResources().getIdentifier(buttonID, "id", "com.pol.happyflight");
            Log.w(TAG, resID + "");
            ImageButton button = v.findViewById(resID);
            final int idNum = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onButtonClick(view, idNum);
                }
            });

            buttons[i] = button;
        }

    }

    private void onButtonClick(final View view, final int i) {

        db.collection("Flight_crash")
                .document(GameRoom.roomId).collection("GameStat").document("Status")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (!(((HashMap<String,ArrayList<Boolean>>) document.get("UserVisited")).get(address)).get(i)) {
                                goToPosition(i);
                                buttons[i].setAlpha(128);
                            }
                            else incorrectPosition(view);
                        }
                    }
                });
    }

    private void incorrectPosition(View view) {
        Toast.makeText(view.getContext(), "Already been visited" , Toast.LENGTH_SHORT).show();
    }

    private void goToPosition(final int pos) {
        ObjectAnimator animX = ObjectAnimator.ofFloat(plane, "x", buttons[pos].getLeft()+25);
        ObjectAnimator animY = ObjectAnimator.ofFloat(plane, "y", buttons[pos].getTop()+25);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(animX, animY);
        animSetXY.setDuration(500);
        animSetXY.start();


        final DocumentReference docRef =
                db.collection("Flight_crash")
                        .document(GameRoom.roomId).collection("GameStat").document("Status");
                docRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if ((Boolean)document.get("Ready")){
                                HashMap<String,Long> newPos = new HashMap<>();
                                newPos = (HashMap<String,Long>) document.get("Destination");
                                newPos.put(address, (long) pos);
                                HashMap<String,Object> data = new HashMap<>();
                                data.put("Destination",newPos);
                                Log.w(TAG,"Supposed set");
                                docRef.set(data,SetOptions.merge());
                            }
                        }
                    }
                });
    }

    private int getCurrentPosition() {
        int pos = 0;

        //FIREBASE CODE HERE

        return pos;
    }


    private HashMap<String, Pair<Float, Float>> theAlgorithm(Map<String, Integer> currents, Map<String, Integer> destinations) {
        HashMap<String, Pair<Float, Float>> res = new HashMap<>();

        //Transform Vectors into Lines
        HashMap<String, Pair<Float, Float>> lines = getLines(currents, destinations);

        //Change from MAC to id or reverse
        Pair<HashMap<String,Integer>, ArrayList<String>> conversion = Pair.create(new HashMap<String, Integer>(), new ArrayList<String>());
        ArrayList<Pair<Float, Float>> lineArray = new ArrayList<Pair<Float, Float>>();
        ArrayList<Pair<Pair<Float, Float>, Pair<Float, Float>>> vectorArray = new ArrayList<>();

        Iterator it = lines.entrySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            conversion.first.put(pair.getKey().toString(), i);
            conversion.second.set(i, pair.getKey().toString());
            i++;

            //Create Arrays to substitute HashMaps
            Pair l = lines.get(pair.getKey().toString());
            lineArray.add(l);
            Pair v = Pair.create(currents.get(pair.getKey().toString()), destinations.get(pair.getKey().toString()));
            vectorArray.add(v);
        }

        //Make lines collide
        ArrayList<ArrayList<Pair<Pair<Integer, Integer>, Pair<Float,Float>>>> collisionList = collide(lineArray, vectorArray);

        //Sort collisions by how apart they are from their origin
        ArrayList<Pair<Pair<Integer, Integer>, Pair<Float, Float>>> sortedList = sortCollisions(collisionList, vectorArray);

        //Destroy all planes that collide
        HashMap<Integer, Pair<Float, Float>> auxDestroyed = getDestroyedPlanes(sortedList, vectorArray.size());
        it = auxDestroyed.entrySet().iterator();

        //Change the data structure to the return type
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            res.put(conversion.second.get((int) pair.getKey()), (Pair<Float,Float>) pair.getValue());
        }

        return res;
    }

    private HashMap<Integer, Pair<Float, Float>> getDestroyedPlanes(ArrayList<Pair<Pair<Integer, Integer>, Pair<Float, Float>>> sortedList, int numPlanes) {
        ArrayList<Boolean> isPlaneDestroyed = new ArrayList<>(Collections.nCopies(numPlanes, false));
        HashMap<Integer, Pair<Float, Float>> destroyed = new HashMap<>();

        for (int i = 0; i < sortedList.size(); i++) {
            if (! isPlaneDestroyed.get(sortedList.get(i).first.second) &&
                ! isPlaneDestroyed.get(sortedList.get(i).first.first)) {

                isPlaneDestroyed.set(sortedList.get(i).first.first, true);
                destroyed.put(sortedList.get(i).first.first, sortedList.get(i).second);
            }
        }

        return destroyed;
    }

    private ArrayList<Pair<Pair<Integer, Integer>, Pair<Float, Float>>> sortCollisions(ArrayList<ArrayList<Pair<Pair<Integer, Integer>, Pair<Float, Float>>>> collisionList,
                                                                                       ArrayList<Pair<Pair<Float, Float>, Pair<Float, Float>>> vectorArray) {
        //Base cases
        if (collisionList.size() == 0) {
            return new ArrayList<>();
        } else if (collisionList.size() == 1) {
            return sortPlaneCollisions(collisionList.get(0), vectorArray);
        }

        //Divide and conquer
        ArrayList<Pair<Pair<Integer, Integer>, Pair<Float, Float>>> l1 = sortCollisions((ArrayList) collisionList.subList(0, collisionList.size() / 2), vectorArray);
        ArrayList<Pair<Pair<Integer, Integer>, Pair<Float, Float>>> l2 = sortCollisions((ArrayList) collisionList.subList(collisionList.size() / 2, collisionList.size()), vectorArray);

        return mergeCollisions(l1, l2, vectorArray);
    }

    private ArrayList<Pair<Pair<Integer, Integer>, Pair<Float, Float>>> sortPlaneCollisions(ArrayList<Pair<Pair<Integer, Integer>, Pair<Float, Float>>> planeCollisions,
                                                                                            ArrayList<Pair<Pair<Float, Float>, Pair<Float, Float>>> vectorArray) {
        int i, j;
        i = j = 0;

        if (planeCollisions.size() <= 1) {
            return planeCollisions;
        }

        ArrayList<Pair<Pair<Integer, Integer>, Pair<Float, Float>>> l1 = (ArrayList) planeCollisions.subList(0, planeCollisions.size() / 2);
        ArrayList<Pair<Pair<Integer, Integer>, Pair<Float, Float>>> l2 = (ArrayList) planeCollisions.subList(planeCollisions.size() / 2, planeCollisions.size());

        return mergeCollisions(l1, l2, vectorArray);

    }

    private ArrayList<Pair<Pair<Integer, Integer>, Pair<Float, Float>>> mergeCollisions(ArrayList<Pair<Pair<Integer, Integer>, Pair<Float, Float>>> l1,
                                                                                        ArrayList<Pair<Pair<Integer, Integer>, Pair<Float, Float>>> l2,
                                                                                        ArrayList<Pair<Pair<Float, Float>, Pair<Float, Float>>> vectorArray) {
        int i, j;
        i = j = 0;

        ArrayList<Pair<Pair<Integer, Integer>, Pair<Float, Float>>> res = new ArrayList<>(l1.size() + l2.size());

        while ((i < l1.size()) && (j < l2.size())) {
            Float d1, d2;
            d1 = euclideanDistance(l1.get(i).second, vectorArray.get(l1.get(i).first.first).first);
            d2 = euclideanDistance(l2.get(j).second, vectorArray.get(l2.get(j).first.first).first);
            if (d1 < d2) {
                res.set(i + j, l1.get(i));
                i++;
            }
            else {
                res.set(i + j, l2.get(j));
                j++;
            }
        }

        while (i < l1.size()) {
            res.set(i + j, l1.get(i));
            i++;
        }

        while (j < l2.size()) {
            res.set(i + j, l2.get(j));
            j++;
        }

        return res;
    }

    private Float euclideanDistance(Pair<Float, Float> a, Pair<Float, Float> b) {
        Float x = b.first - a.first;
        Float y = b.second - a.second;
        return (float) Math.sqrt(x*x + y*y);
    }

    private ArrayList< ArrayList< Pair< Pair<Integer, Integer>, Pair< Float, Float> > > > collide(ArrayList<Pair<Float, Float>> lines, ArrayList<Pair<Pair<Float, Float>, Pair<Float, Float>>> vectors) {
        ArrayList<ArrayList<Pair<Pair<Integer, Integer>, Pair<Float, Float>>>> collisionList = new ArrayList<>(lines.size());

        for (int i = 0; i < lines.size(); i++) {
            for (int j = i + 1; i < lines.size(); j++) {
                Pair<Float, Float> col = getCollisionPoint(lines.get(i), lines.get(j));

                if (isInVector(col, vectors.get(i)) && isInVector(col,vectors.get(j))) {
                    collisionList.get(i).add(Pair.create(Pair.create(i, j), col));
                    collisionList.get(j).add(Pair.create(Pair.create(j, i), col));
                }
            }
        }

        return collisionList;
    }

    private Boolean isInVector(Pair<Float, Float> point, Pair< Pair<Float, Float>, Pair<Float, Float>> vector) {
        Float x = (point.first - vector.first.first);
        Float y = (point.second - vector.first.second);
        Float xt = (vector.second.first - vector.first.first);
        Float yt = (vector.second.second - vector.first.second);
        Float r = ((float) Math.sqrt(x*x + y*y)) / ((float) Math.sqrt(xt*xt + yt*yt));
        return (r > 0) && (r <= 1);
    }

    private Pair<Float, Float> getCollisionPoint(Pair<Float, Float> line1, Pair<Float, Float> line2) {
        /*
        y = mx + n;
        y' = m'x' + n';
        y' = y; x' = x;
        m'x' + n' = mx + n;
        (m' - m)x = n - n';

        x = (n - n') / (m' - m)
         */

        Float x = (line2.second - line1.second) / (line1.first - line2.first);

        // y = m*x + n
        Float y = line1.first * x + line1.second;

        return Pair.create(x,y);
    }

    private HashMap<String, Pair<Float, Float>> getLines(Map<String, Integer> currents, Map<String, Integer> destinations) {
        HashMap<String, Pair<Float, Float>> lines = new HashMap<String, Pair<Float, Float>>();

        Iterator it = currents.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            /*
            String d = "destination".concat(Integer.toString(currents.get(pair.getKey())));
            int resID = getResources().getIdentifier(d, "id", "com.pol.happyflight");
            Button b = (Button) view.findViewById(resID);
            */

            //Current position's button (Need to get its position)
            String buttonID1 = "destination".concat(Integer.toString(currents.get(pair.getKey())));
            int resID1 = getResources().getIdentifier(buttonID1, "id", "com.pol.happyflight");
            ImageButton b1 = (ImageButton) view.findViewById(resID1);

            //Destination's button (Need to get its position)
            String buttonID2 = "destination".concat(Integer.toString(destinations.get(pair.getKey())));
            int resID2 = getResources().getIdentifier(buttonID2, "id", "com.pol.happyflight");
            ImageButton b2 = (ImageButton) view.findViewById(resID2);

            Float slope = (b2.getY() - b1.getY()) / (b2.getX() - b1.getX());
            Float axisCut = b2.getY() - slope * b2.getX();

            lines.put((String) pair.getKey(), Pair.create(slope, axisCut));
        }

        return lines;
    }
}
