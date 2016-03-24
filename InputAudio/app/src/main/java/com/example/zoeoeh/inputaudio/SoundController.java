package com.example.zoeoeh.inputaudio;

import android.content.Context;
import android.widget.MediaController;
import android.media.session.MediaSession;

/**
 * Created by Zoeoeh on 24/03/2016.
 */
public class SoundController extends MediaController {

    public SoundController(Context context) {
        super(context);
    }

    // overriding the hide method stops the view from disappearing after 3 seconds
    public void hide(){}
}
