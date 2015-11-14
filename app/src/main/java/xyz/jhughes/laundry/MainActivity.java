package xyz.jhughes.laundry;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import xyz.jhughes.laundry.FragmentPagerAdapter.AppSectionsPagerAdapter;
import xyz.jhughes.laundry.LaundryParser.Constants;

public class MainActivity extends AppCompatActivity {

    /**
     * Managing our tabs
     */
    private ViewPager viewPager;

    /**
     * The layout we use for the drawer, which is simply a ListView in our case.
     */
    private DrawerLayout mDrawerLayout;

    /**
     * Managing whether it'currentRoom open or closed
     */
    private ActionBarDrawerToggle mDrawerToggle;

    /**
     * The list of rooms available
     */
    private ListView mDrawerList;

    /**
     * The current room
     */
    private static String currentRoom;

    private AppSectionsPagerAdapter appSectionsPagerAdapter;

    private  Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        //Normal onCreate method calls.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("xyz.jhughes.laundry", MODE_PRIVATE);

        currentRoom = sharedPreferences.getString("lastRoom", "Cary West");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(currentRoom);

        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        //We can't really do anything if the ActionBar is null.
        //This should never happen though, so we shouldn't worry too much about it.
        //Maybe someone can find a case where this does happen and we can fix it.
        if (actionBar == null) {
            return;
        }

        //Set up the ActionBar with the correct icons and color
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, Constants.getListOfRooms()));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        //Respond properly when the drawer is opened and closed.
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                mDrawerToggle.setDrawerIndicatorEnabled(true);
                toolbar.setTitle(currentRoom);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu
            }

            public void onDrawerOpened(View drawerView) {
                mDrawerToggle.setDrawerIndicatorEnabled(false);
                toolbar.setTitle("Areas");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu
            }
        };

        //Set up being able to open and close the drawer
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        //Setting up the ViewPager so that we can properly respond to what tab is selected and when we change.

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        appSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager(), currentRoom);
        viewPager.setAdapter(appSectionsPagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * This method is called when we click on a room in the Drawer.
     */
    private class DrawerItemClickListener implements AdapterView.OnItemClickListener {

        /**
         * This method is called when we click on something in the drawer.
         *
         * We use this method in order to update the String of what is selected, to tell the Fragments to refresh their
         * data, and to close the drawer.
         *
         * @param parent - The AdapterView that contains a list of the rooms.
         * @param view - The View of the AdapterView
         * @param position - The int representation in the ListView that was clicked (0-based, as with all arrays, etc)
         * @param id - The row ID of the item clicked. We have no use for this right now.
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            currentRoom = (String) parent.getItemAtPosition(position);
            toolbar.setTitle(currentRoom);
            appSectionsPagerAdapter.setSelected(currentRoom);
            appSectionsPagerAdapter.notifyDataSetChanged();

            SharedPreferences.Editor e = getSharedPreferences("xyz.jhughes.laundry", MODE_PRIVATE).edit();
            e.putString("lastRoom", currentRoom);
            e.apply();

            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    public static String getSelected() {
        return currentRoom;
    }



}
