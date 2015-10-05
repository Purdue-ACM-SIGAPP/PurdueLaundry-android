package xyz.jhughes.laundry;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import xyz.jhughes.laundry.FragmentPagerAdapter.AppSectionsPagerAdapter;
import xyz.jhughes.laundry.LaundryParser.Constants;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener {

    /**
     * Managing our tabs
     */
    private ViewPager viewPager;

    /**
     * The layout we use for the drawer, which is simply a ListView in our case.
     */
    private DrawerLayout mDrawerLayout;

    /**
     * Managing whether it's open or closed
     */
    private ActionBarDrawerToggle mDrawerToggle;

    /**
     * The list of rooms available
     */
    private ListView mDrawerList;

    /**
     * The current room
     */
    private static String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Normal onCreate method calls.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //We go through pretty standard methods of setting up a drawer.
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, Constants.getListOfRooms()));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        //Used to manage the Fragments
        AppSectionsPagerAdapter appSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        //Store the ActionBar locally so we don't need to keep making ugly function calls.
        //Function calls are expensive on Android compared to the desktop.
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
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#b1810b")));
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#b1810b")));

        //Respond properly when the drawer is opened and closed.
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                mDrawerToggle.setDrawerIndicatorEnabled(true);
                actionBar.setTitle("Purdue Laundry");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu
            }

            public void onDrawerOpened(View drawerView) {
                mDrawerToggle.setDrawerIndicatorEnabled(false);
                actionBar.setTitle("Areas");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu
            }
        };

        //Set up being able to open and close the drawer
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        //Setting up the ViewPager so that we can properly respond to what tab is selected and when we change.
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(appSectionsPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        //Adding all the tabs to the ActionBar
        for (int i = 0; i < appSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(actionBar.newTab().setText(appSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
        }

        //Defaulting the default room to Cary West
        s = "Cary West";

        //We use this if to determine if we can set the color of the status
        //It is only supported on Lollipop or higher.
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#b1810b")); //This color is the main Purdue gold
            window.setNavigationBarColor(Color.parseColor("#b1810b"));
        }
    }

    /**
     * We use this method to open and close the drawer when the hamburger/home icon are clicked.
     *
     * @param item - the item in the ActionBar/ToolBar clicked.
     * @return - Whether is succeeded or not.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mDrawerLayout.closeDrawer(mDrawerList);
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    /**
     * We currently have no use for this method, but if we want to add buttons to the ActionBar/ToolBar in the future,
     * we will need this method.
     *
     * @param menu - the current menu
     * @return - true, since there are no items to add, it will always succeed.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * We use this method to update the hamburger menu icon!
     *
     * @param savedInstanceState - the currently saved state
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    /**
     * We use this to update the hamburger menu icon!
     *
     * @param newConfig - The configuration passed by the Drawer
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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
            s = (String) parent.getItemAtPosition(position);

            /*WasherFragment washerFragment = (WasherFragment) getSupportFragmentManager().findFragmentByTag("WasherFragment");
            washerFragment.refreshList();

            DryerFragment dryerFragment = (DryerFragment) getSupportFragmentManager().findFragmentByTag("DryerFragment");
            dryerFragment.refreshList();*/

            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    /**
     * This method is called when the current tab is switched to.
     *
     * We use this method to let the ViewPager know where we are, and we can then react accordingly.
     *
     * @param tab - The currently selected tab in the Tab View
     * @param fragmentTransaction - The Fragment associated with the current tab
     */
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    /**
     * In our case, we don't really need to do anything with this method, but it is required by TabListener
     *
     * @param tab - The currently selected tab in the Tab View
     * @param fragmentTransaction - The Fragment associated with the current tab
     */
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        //Do Nothing!
    }

    /**
     * In our case, we don't really need to do anything with this method, but it is required by TabListener
     *
     * @param tab - The currently selected tab in the Tab View
     * @param fragmentTransaction - The Fragment associated with the current tab
     */
    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        //Do Nothing!
    }

    /**
     * This method returns the currently selected room for use in the Fragments on refresh
     *
     * This should probably never be done and there is most likely a better way.
     *
     * @return the selected room in String form
     */
    public static String getSelected() {
        return s;
    }

}
