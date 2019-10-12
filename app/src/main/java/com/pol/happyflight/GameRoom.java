package com.pol.happyflight;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.pol.happyflight.GamesFragments.FlightCrush;
import com.pol.happyflight.GamesFragments.Passengers;
import com.pol.happyflight.GamesFragments.Trivia;

import static java.lang.Thread.sleep;

public class GameRoom extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_room);

        Bundle extras = getIntent().getExtras();
        String name = extras.getString("name");
        loadGame(name);
    }


    private void loadGame(String name){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = null;

        switch(name){
            case "Tetris":
                fragment = new Passengers();
                break;
            case "Parchís":
                fragment = new Trivia();
                break;
            case "Avions":
                fragment = new FlightCrush();

                break;
        }

        fragmentTransaction.add(R.id.fragmentLayout, fragment);
        fragmentTransaction.commit();
    }
}
