package com.example.zoeoeh.ETuner;

import android.content.Context;
import android.widget.MediaController;

/**
 * Created by Zoeoeh on 24/03/2016.
 * Class used to override MediaController hide method to hide when required
 */
public class SoundController extends MediaController {

    public SoundController(Context context) {
        super(context);
    }

    // overriding the hide method stops the view from disappearing after 3 seconds
    public void hide(){}

    public void inVisible()
    {
        // hides controller when needed
        super.hide();
    }
}
