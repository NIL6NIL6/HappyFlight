package com.pol.happyflight.Classes;

import android.graphics.drawable.Drawable;

public class RoomContainer   {
    private Drawable img;
    private String name;
    private String tag;
    public RoomContainer(String name, Drawable img, String tag) {
        this.name = name;
        this.img = img;
        this.tag = tag;
    }

    public Drawable getImg() {
        return img;
    }

    public void setImg(Drawable img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
