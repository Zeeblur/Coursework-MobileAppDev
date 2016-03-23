package com.example.zoeoeh.inputaudio;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

// import sound pool

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


// class uses SensorEventListener interface
public class PlayTune extends Fragment {

    private ArrayList<AudioClip> clipList;
    private ListView clipView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        // Replaces setContentView - set the content to the XML activity containing the custom surface view
        View myView = inflater.inflate(R.layout.activity_play_tune, container, false);

        // set clipView to list from xml
        clipView = (ListView) myView.findViewById(R.id.clipList);

        // instantiate list
        clipList = new ArrayList<AudioClip>();

        // link to database and retrieve clips
        getSongList();

        // sort list into alphabetical order from title
        Collections.sort(clipList, new Comparator<AudioClip>() {
            @Override
            public int compare(AudioClip lhs, AudioClip rhs) {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        });

        // create clipAdapter instance and pass in the listview
        ClipAdapt clipAd = new ClipAdapt(getContext(), clipList);
        clipView.setAdapter(clipAd);

        return myView;
    }

    public void getSongList()
    {
        // Content Resolver allows access to files from media store database
        ContentResolver soundResolver = getContext().getContentResolver();
        Uri soundUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        // initialise database cursor
        Cursor soundCursor = soundResolver.query(soundUri, null, null, null, null);

        if(soundCursor!=null && soundCursor.moveToFirst())
        {
            int titleColumn = soundCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = soundCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistColumn = soundCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            // iterate through database and add audioclips to list
            do {
                long thisId = soundCursor.getLong(idColumn);
                String thisTitle = soundCursor.getString(titleColumn);
                String thisArtist = soundCursor.getString(artistColumn);
                clipList.add(new AudioClip(thisId, thisTitle, thisArtist));
            }
            while (soundCursor.moveToNext());

        }

    }

}
