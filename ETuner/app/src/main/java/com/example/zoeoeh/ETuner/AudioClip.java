package com.example.zoeoeh.ETuner;

/**
 * Created by Zoe Wall on 23/03/2016.
 * Used to store and retrieve data for an audio clip
 */
public class AudioClip {

    // data to store for each track
    private long id;
    private String title;
    private String description;

    public AudioClip(long clipID, String clipTitle, String clipDesc)
    {
        id = clipID;
        title = clipTitle;
        description = clipDesc;
    }

    // getters for values
    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getDescription(){return description;}
}
