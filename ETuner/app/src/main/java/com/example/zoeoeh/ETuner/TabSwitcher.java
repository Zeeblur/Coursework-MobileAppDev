package com.example.zoeoeh.ETuner;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class TabSwitcher extends AppCompatActivity {

    private static boolean listDirty = false;
    private static Context mContext;
    private static PageAdapt adapter;

    public static void setListDirty(Boolean value)
    {
        listDirty = value;
    }

    public static Context getmContext()
    {
        return mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_switcher);

        mContext = this;

        // initialise toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Tab 1"));
        tabLayout.addTab(tabLayout.newTab().setText("Tab 2"));
        tabLayout.addTab(tabLayout.newTab().setText(("Test")));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new PageAdapt(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 1 && listDirty)
                {
                    PlayTune myTune = (PlayTune)adapter.getItem(1);
                    myTune.populateClipListView();
                    listDirty = false;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {
                // TODO destroy grpahic tab
                /*
                if (tab.getPosition() == 2) {
                    Toast.makeText(getBaseContext(), tab.getPosition() + " is this 2", Toast.LENGTH_LONG).show();
                    FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                    trans.detach(adapter.getItem(2));
                    trans.commit();
                } */
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {
               /* if (tab.getPosition() == 2) {
                    FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                    trans.attach(adapter.getItem(2));
                    trans.commit();
                } */

                if (tab.getPosition() == 1 && listDirty)
                {
                    PlayTune myTune = (PlayTune)adapter.getItem(1);
                    myTune.populateClipListView();
                    listDirty = false;
                }

            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId()) {
            case R.id.action_shuffle:
                //shuffle
                break;
            case R.id.action_play:
                PlayTune myTune = (PlayTune)adapter.getItem(1);
                myTune.exitPlayer();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // click handler for fragment xml clip
    public void soundClicked(View view)
    {
        PlayTune myTune = (PlayTune)adapter.getItem(1);
        myTune.clipPicked(view);
        Toast.makeText(getmContext(), "mmhmmm clipPicked", Toast.LENGTH_SHORT).show();
    }

    // exit with app closing player
    @Override
    public void onBackPressed() {
        PlayTune myTune = (PlayTune)adapter.getItem(1);
        myTune.exitPlayer();
    }



}
