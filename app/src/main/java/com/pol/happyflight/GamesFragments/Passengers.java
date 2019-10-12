package com.pol.happyflight.GamesFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.pol.happyflight.R;

import static java.lang.Math.random;
import static java.lang.Thread.sleep;

public class Passengers  extends Fragment {
    ImageButton rotateB, leftB, rightB, downB;
    boolean goRight = false, goLeft = false, goRotate = false;
    boolean[][] Board = new boolean[7][40];
    int speed = 500, x = 1, y = 1;
    boolean collision = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.passengers, container, false);

        rotateB = view.findViewById(R.id.rotateButton);
        rotateB.setOnClickListener(rotateEvent);
        leftB = view.findViewById(R.id.leftButton);
        leftB.setOnClickListener(leftEvent);
        rightB = view.findViewById(R.id.rightButton);
        rightB.setOnClickListener(rightEvent);
        downB = view.findViewById(R.id.downButton);
        downB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    speed = 500;
                    Log.d("HOLA","500-----------");
                    return true;
                }
                speed = 250;
                Log.d("HOLA","250-----------");
                return false;
            }
        });

        for(int i = 0; i < Board.length; ++i){
            for(int j = 0; j < Board[0].length; ++j){
                Board[i][j] = false;
            }
        }

        //Match

        boolean end = false;

        while(!end) {
            boolean piece[][] = pieceGenerator();

            if(collision) {
                break;
            }
            try {
                Thread.sleep(speed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ++y;
            if(goLeft && x > 0){
                --x;
                if (nextMoveAvailable(piece)) movePiece(piece);
                else {
                    break;
                }
            }
            if(goRight && x < 6){
                ++x;
                if (nextMoveAvailable(piece)) movePiece(piece);
                else {
                    break;
                }
            }
            if(goRotate) rotatePiece(piece);

            if(collision) break;

        }
        return view;
    }

    private void rotatePiece(boolean[][] pi) { //check not collision
        int w = pi.length;
        boolean rpi[][] = new boolean[w][w];
        for (int i = 0; i<w; i++){
            for(int j = 0; j<w; j++) rpi[i][j] = pi[w-j-1][i];
        }

        for(int i = 0; i < pi.length; ++i){
            for(int j = 0; j < pi[i].length; j++) {
                pi[i][j] = rpi[i][j];
                if(Board[x+i-1][y+j-1] && pi[i][j]) collision = true;
            }
        }


    }

    private void movePiece(boolean[][] pi) {
        int k = -1; int l = -1;
        for(int i = x-(pi.length)/2; i < x+(pi.length)/2; ++i){
            ++k;
            for(int j = y-(pi[0].length)/2; j < y+(pi[0].length)/2; ++j) {
                ++l;
                Board[i][j] = pi[k][l] ;
            }
        }
    }

    View.OnClickListener rightEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("HOLA","RIGHT");
            goRight = true;
        }
    };
    View.OnClickListener leftEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("HOLA","LEFT");
            goLeft = true;
        }
    };
    View.OnClickListener rotateEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("HOLA","ROTAAR");
            goRotate = true;
        }
    };

    public boolean pieceEnter(boolean[][] pi){
        for (int i = 0; i < pi.length; ++i){
            for(int j = 0; j < pi[0].length; ++j) {
                if (Board[i][j] && pi[i][j]) {
                    collision = true;
                    return false;
                }
                Board[i][j] = pi[i][j];
            }
        }
        return true;
    }

    public boolean nextMoveAvailable(boolean[][] pi){
        for(int i = x-(pi.length)/2; i < x+(pi.length)/2; ++i){
            for(int j = y-(pi[0].length)/2; j < y+(pi[0].length)/2; ++j){
                if(pi[i][j] && Board[i][j]) {
                    collision = true;
                    return false;
                }
            }
        }
        return true;

    }

    public boolean[][] pieceGenerator(){
        collision = false;
        x = 1; y = 1;
        boolean res[][] =  {{false,false,false},{false,false,false},{false,false,false}};
        double i = random()*7;
        if(i < 1.0) {
            res[1][0] = true; res[1][1] = true; res[1][2] = true;
            return res;
        }
        if(i < 2.0) {
            res[0][1] = true; res[1][1] = true; res[1][2] = true;
            return res;
        }
        if(i < 3.0) {
            res[0][0] = true; res[0][1] = true; res[2][1] = true;
            return res;
        }
        if(i < 4.0) {
            res[0][2] = true; res[2][1] = true; res[2][2] = true;
            return res;
        }
        if(i < 5.0) {
            res[0][0] = true; res[1][1] = true; res[2][0] = true;
            return res;
        }
        if(i < 6.0) {
            res[0][0] = true; res[1][1] = true; res[2][1] = true;
            return res;
        }
        res[0][1] = true; res[1][1] = true; res[2][0] = true;
        return res;
    }
}