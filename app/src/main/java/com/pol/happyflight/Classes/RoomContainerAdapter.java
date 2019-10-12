package com.pol.happyflight.Classes;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pol.happyflight.GameRoom;
import com.pol.happyflight.R;
import com.pol.happyflight.RoomsList;

import java.util.List;


public class RoomContainerAdapter extends RecyclerView.Adapter<RoomContainerAdapter.RoomContainerViewHolder>{

    private List<RoomContainer> roomContainerList;

    public RoomContainerAdapter(List<RoomContainer> bookList) {
        this.roomContainerList = bookList;
    }


    @Override
    public RoomContainerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.room_container, parent, false);

        return new RoomContainerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RoomContainerViewHolder holder, final int position) {
        holder.title.setText(roomContainerList.get(position).getName());
        holder.img.setImageDrawable(roomContainerList.get(position).getImg());
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("AAAinfo",roomContainerList.get(position).getTag() );
                RoomsList.enterRoom(roomContainerList.get(position).getName(), roomContainerList.get(position).getTag());
            }
        });
    }

    @Override
    public int getItemCount() {
        return roomContainerList.size();
    }

    public class RoomContainerViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView img;
        public RoomContainerViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            img = (ImageView) view.findViewById(R.id.img);
        }

    }

}

