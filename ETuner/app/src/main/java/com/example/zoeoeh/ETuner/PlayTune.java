package com.example.zoeoeh.ETuner;
import com.example.zoeoeh.ETuner.SoundService.MusicBinder;


import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.util.Log;
import android.widget.MediaController.MediaPlayerControl;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Zoe Wall on 23/03/2016.
 * Fragment for Play Tab, used to display user's voice recordings and allow playback of them.
 */
public class PlayTune extends Fragment implements MediaPlayerControl {

    private static ArrayList<AudioClip> clipList;
    private static ListView clipView;
    private static ContentResolver soundResolver;
    private static Uri soundUri;

    public static SoundController controller;


    // instance variables for playback music
    private static SoundService myService;
    private static Intent playIntent;
    private static boolean musicBound = false;
    private boolean paused=false, playbackPaused=false;
    private static boolean pageInvisible = false;

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

        // add items to list
        populateClipListView();

        // set music controller
        setController();

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
            int albumColumn = soundCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);

            // iterate through database and add audioclips to list
            do {
                long thisId = soundCursor.getLong(idColumn);
                String thisTitle = soundCursor.getString(titleColumn);
                String thisArtist = soundCursor.getString(artistColumn);
                if (soundCursor.getString(albumColumn).equals(Recorder.albumName))
                {
                    clipList.add(new AudioClip(thisId, thisTitle, thisArtist));
                }
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
        if (paused)
        {
            setController();
            paused=false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        paused=true;
    }

    @Override
    public void onStop()
    {
        controller.inVisible();
        super.onStop();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            // If we are becoming invisible, then...
            if (!isVisibleToUser) {
                Log.d("MyFragment", "Not visible anymore.  Stopping audio.");
                pageInvisible = true;
                paused=true;
                playbackPaused= true;

                // pause sounds when off screen
                myService.pausePlayer();

                // when tab is invisible use super.hide to hide the controller.
                controller.inVisible();
            }
            else
            {
                pageInvisible = false;
                paused = false;
                setController();

                // show controller when tab is visible.
                controller.show(0);
            }
        }
    }

    @Override
    public void onDestroy() {
        TabSwitcher.getmContext().stopService(playIntent);
        myService=null;
        super.onDestroy();
    }

    // stops service and exits application
    public void exitPlayer()
    {
        TabSwitcher.getmContext().stopService(playIntent);
        myService=null;
        System.exit(0);
    }

    // click handler for clip view
    public void clipPicked(View view) {
        myService.pickClip(Integer.parseInt(view.getTag().toString()));

        // change colour when picked to show selection

        // resets current playing colour
        myService.resetColour();
        // change picked colour
        view.setBackgroundColor(view.getResources().getColor(R.color.darkerBlue));
        myService.startPlaying();
        if (playbackPaused)
        {
            setController();
            playbackPaused = false;
        }
        //controller.show(0);
    }

    private void setController() {
        if (controller == null)
        {
            controller = new SoundController(myView.getContext());
        }

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
        controller.setAnchorView(myView.findViewById(R.id.spaceForController));
        controller.setEnabled(true);

    }

    public static boolean getVisible()
    {
        return pageInvisible;
    }

    private void playNext()
    {
        myService.playNext();

        if (playbackPaused)
        {
            setController();
            playbackPaused = false;
        }
    }

    private void playPrev()
    {
        myService.playPrev();

        if (playbackPaused)
        {
            setController();
            playbackPaused = false;
        }
    }

    // Implementation of media player controller interface
    @Override
    public void start() {
        myService.go();
    }

    @Override
    public void pause() {
        playbackPaused = true;
        myService.pausePlayer();
    }

    @Override
    public int getDuration() {
        if(myService!=null && musicBound && myService.isPng())
        return myService.getDur();
        else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(myService!=null && musicBound && myService.isPng())
        return myService.getPosn();
        else return 0;
    }

    @Override
    public void seekTo(int pos) {
        myService.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if(myService!=null && musicBound)
        return myService.isPng();
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
