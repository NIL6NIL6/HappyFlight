package com.pol.happyflight.Classes;

public class Room {
    String id;
    boolean enCurs;
    int maxJug;
    int minJug;
    int numJug;

    public Room(String id, boolean enCurs, int maxJug, int minJug,int numJug) {
        this.id = id;
        this.enCurs = enCurs;
        this.maxJug = maxJug;
        this.minJug = minJug;
        this.numJug = numJug;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isEnCurs() {
        return enCurs;
    }

    public void setEnCurs(boolean enCurs) {
        this.enCurs = enCurs;
    }

    public int getMaxJug() {
        return maxJug;
    }

    public void setMaxJug(int maxJug) {
        this.maxJug = maxJug;
    }

    public int getMinJug() {
        return minJug;
    }

    public void setMinJug(int minJug) {
        this.minJug = minJug;
    }

    public int getNumJug() {
        return numJug;
    }

    public void setNumJug(int numJug) {
        this.numJug = numJug;
    }
}
