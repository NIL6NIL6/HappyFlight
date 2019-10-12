package com.pol.happyflight.Classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.pol.happyflight.R;

public class ImageAdapter extends BaseAdapter {

    int image_human = R.drawable.human;

    Context ctx;
    ImageAdapter(Context ctx){
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return image_human;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View gridView = view;

        if(gridView == null){
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.activity_firebase_test,  null);
        }
        ImageView i1 =(ImageView)gridView.findViewById(R.id.myImage);
        i1.setImageResource(image_human);
        return gridView;
    }
}
