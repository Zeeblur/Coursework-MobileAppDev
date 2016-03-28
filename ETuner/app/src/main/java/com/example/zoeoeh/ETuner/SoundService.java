package com.example.zoeoeh.ETuner;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Zoeoeh on 23/03/2016.
 */
public class SoundService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener
{

    private static MediaPlayer myPlayer;
    private static ArrayList<AudioClip> clips;
    private int currentPos;

    private final IBinder musBind = new MusicBinder();

    private String TAG = "Sound Service";

    @Override
    public void onCreate()
    {
        super.onCreate();

        // initialise position
        currentPos = 0;

        // initialise media player
        myPlayer = new MediaPlayer();


        // initialise settings for media player
        initMediaPlayer();
    }

    public void initMediaPlayer()
    {
        // sets the player's properties


        // if device is idle continue playing
        myPlayer.setWakeMode(TabSwitcher.getmContext(), PowerManager.PARTIAL_WAKE_LOCK);
        myPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // set listeners to this class as these are implemented below
        myPlayer.setOnPreparedListener(this);
        myPlayer.setOnCompletionListener(this);
        myPlayer.setOnErrorListener(this);

    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        return musBind;
    }

    @Override
    public boolean onUnbind(Intent arg0)
    {
        // when music is unbound stop the player and free mem

        myPlayer.stop();
        myPlayer.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(myPlayer.getCurrentPosition() > 0){
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp)
    {
        // when prepared start playback
        mp.start();
        PlayTune.controller.show(0);
    }

    public void setList(ArrayList<AudioClip> value)
    {
        // setter for list
        clips = value;
    }

    public class MusicBinder extends Binder {
        SoundService getService() {
            return SoundService.this;
        }
    }

    public void startPlaying()
    {
        myPlayer.reset(); // incase player was already in use

        // get clip from list, model as a URI
        AudioClip playingClip = clips.get(currentPos);

        // iterate through list of clip layouts. Find the playing clip and change it's colour
        for (int i = 0; i < ClipAdapt.myClipLayouts.size(); ++i )
        {
            if (currentPos == ClipAdapt.myClipLayouts.get(i).getTag()){
                ClipAdapt.myClipLayouts.get(i).setBackgroundColor(getResources().getColor(R.color.darkerBlue));
            }
        }

        long currentClip = playingClip.getID();

        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currentClip);

        try
        {
            myPlayer.setDataSource(TabSwitcher.getmContext(), trackUri);
        }
        catch (IOException e)
        {
            Log.e(TAG, "Error setting data source" + e);
        }

        myPlayer.prepareAsync();
    }

    public void pickClip(int clipIndex)
    {
        // allows user to pick what sound clip to play
        currentPos = clipIndex;
    }

    public int getPosn(){
        return myPlayer.getCurrentPosition();
    }

    public int getDur(){
        return myPlayer.getDuration();
    }

    public boolean isPng(){
        return myPlayer.isPlaying();
    }

    public void pausePlayer(){
        myPlayer.pause();
    }

    public void seek(int posn){
        myPlayer.seekTo(posn);
    }

    public void go(){
        myPlayer.start();
    }

    public void playPrev(){
        // resets current playing colour
        resetColour();

        currentPos--;

        if(currentPos < 0)
        {
            currentPos=clips.size()-1;
        }
        startPlaying();
    }

    public void playNext(){
        // resets current playing colour
        resetColour();
        currentPos++;
        if(currentPos >= clips.size())
        {
            currentPos=0;
        }
        startPlaying();
    }

    public void resetColour()
    {
        // iterate through list of clip layouts and reset the colour to standard background
        for (int i = 0; i < ClipAdapt.myClipLayouts.size(); ++i )
        {
            ClipAdapt.myClipLayouts.get(i).setBackgroundColor(getResources().getColor(R.color.backgroundGrey));
        }
    }

}
