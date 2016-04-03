package com.example.zoeoeh.ETuner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Zoe Wall on 23/03/2016.
 * Created to return a view based on the chosen song to populate the list tab
 */
public class ClipAdapt extends BaseAdapter {

    // local copy of song lists
    private ArrayList<AudioClip> clips;
    private final LayoutInflater clipInflater;
    private static ArrayList<LinearLayout> myClipLayouts = new ArrayList<>();

    public ClipAdapt(Context c, ArrayList<AudioClip> theClips)
    {
        clips = theClips;

        // layout inflater is used to map the songs to the text views
        clipInflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() { return clips.size(); }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int listPosition, View conView, ViewGroup parent) {
        //map to song layout
        LinearLayout clipLayout = (LinearLayout)clipInflater.inflate
                (R.layout.clip, parent, false);

        //get title and artist views
        TextView songView = (TextView)clipLayout.findViewById(R.id.song_title);
        TextView artistView = (TextView)clipLayout.findViewById(R.id.song_artist);
        //get song using position
        AudioClip currSong = clips.get(listPosition);
        //get title and artist strings
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getDescription());
        //set position as tag
        clipLayout.setTag(listPosition);

        // add to list to allow colour control
        myClipLayouts.add(clipLayout);

        return clipLayout;
    }

    // getter for clip layout list
    public static ArrayList<LinearLayout> getMyClipLayouts() { return myClipLayouts; }

}
