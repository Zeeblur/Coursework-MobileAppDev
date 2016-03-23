package com.example.zoeoeh.inputaudio;

/**
 * Created by Zoeoeh on 23/03/2016.
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
