package com.example.zoeoeh.inputaudio;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import layout.TabFragment1;
import layout.TabFragment2;

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
                OpenGLClass tab1 = new OpenGLClass();
                return tab1;
            case 1:
                TabFragment2 tab2 = new TabFragment2();
                return tab2;
            case 2:
                TabFragment1 tab3 = new TabFragment1();
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
