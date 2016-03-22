package com.example.zoeoeh.inputaudio;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Zoeoeh on 21/03/2016.
 */
public class PageAdapt extends FragmentStatePagerAdapter {
    private int numberOfTabs;

    public PageAdapt(FragmentManager fragMan, int tabNums)
    {
        super(fragMan);
        this.numberOfTabs = tabNums;
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                PlayTune tab1 = new PlayTune();
                return tab1;
            case 1:
                Recorder tab2 = new Recorder();
                return tab2;
            case 2:
                OpenGLClass tab3 = new OpenGLClass();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount()
    {
        return numberOfTabs;
    }


}
