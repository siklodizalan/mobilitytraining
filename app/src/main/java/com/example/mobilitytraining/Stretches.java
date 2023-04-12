package com.example.mobilitytraining;

import java.util.ArrayList;

public class Stretches {

    public String stretchName;
    public String description;
    public int duration;
    public String imageLink;
    public boolean twoSided;
    //public ArrayList<String> musclesInvolved;

    public Stretches(String stretchName, String description, int duration, String imageLink, boolean twoSided) {

        this.stretchName = stretchName;
        this.description = description;
        this.duration = duration;
        this.imageLink = imageLink;
        this.twoSided = twoSided;
    }
}
