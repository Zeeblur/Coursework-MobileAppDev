package com.example.zoeoeh.inputaudio;
import com.example.zoeoeh.inputaudio.SoundService.MusicBinder;


import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.widget.MediaController.MediaPlayerControl;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class PlayTune extends Fragment implements MediaPlayerControl {

    private static ArrayList<AudioClip> clipList;
    private static ListView clipView;
    private static ContentResolver soundResolver;
    private static Uri soundUri;

    private static SoundController controller;


    // instance variables for playback music
    private static SoundService myService;
    private static Intent playIntent;
    private static boolean musicBound = false;

    private static View myView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        // Replaces setContentView - set the content to the XML activity containing the custom surface view
        myView = inflater.inflate(R.layout.activity_play_tune, container, false);

        // set clipView to list from xml
        clipView = (ListView) myView.findViewById(R.id.clipList);

        // instantiate list
        clipList = new ArrayList<AudioClip>();

        soundResolver = getActivity().getContentResolver();
        soundUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        populateClipListView();

        myService = new SoundService();
        return myView;
    }

    public void populateClipListView() {
        // link to database and retrieve clips
        if (clipList != null) {
            clipList.clear();
        }

        getSongList();

        // sort list into alphabetical order from title
        Collections.sort(clipList, new Comparator<AudioClip>() {
            @Override
            public int compare(AudioClip lhs, AudioClip rhs) {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        });

        // create clipAdapter instance and pass in the listview
        ClipAdapt clipAd = new ClipAdapt(TabSwitcher.getmContext(), clipList);
        clipView.setAdapter(clipAd);
    }


    public void getSongList() {
        // Content Resolver allows access to files from media store database


        // initialise database cursor
        Cursor soundCursor = soundResolver.query(soundUri, null, null, null, null);

        if (soundCursor != null && soundCursor.moveToFirst()) {
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

        soundCursor.close();
    }

    private ServiceConnection soundConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder = (MusicBinder) service;
            myService = binder.getService();
            myService.setList(clipList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();

        if (playIntent == null) {
            playIntent = new Intent(TabSwitcher.getmContext(), SoundService.class);
            TabSwitcher.getmContext().bindService(playIntent, soundConn, Context.BIND_AUTO_CREATE);
            TabSwitcher.getmContext().startService(playIntent);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        TabSwitcher.getmContext().stopService(playIntent);
        myService=null;
        super.onDestroy();
    }

    public void exitPlayer()
    {
        TabSwitcher.getmContext().stopService(playIntent);
        myService=null;
        System.exit(0);
    }


    // click handler for clip view
    public void clipPicked(View view) {
        myService.pickClip(Integer.parseInt(view.getTag().toString()));
        myService.startPlaying();
    }

    private void setController() {
        controller = new SoundController(TabSwitcher.getmContext());

        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        controller.setMediaPlayer(this);
        controller.setAnchorView(myView.findViewById(R.id.clipList));
        controller.setEnabled(true);
    }

    public void playNext()
    {

    }

    public void playPrev()
    {

    }

    // Implementation of media player controller interface
    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public void seekTo(int pos) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
