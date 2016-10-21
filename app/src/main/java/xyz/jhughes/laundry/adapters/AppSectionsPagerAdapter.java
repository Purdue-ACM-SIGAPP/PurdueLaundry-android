package xyz.jhughes.laundry.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import xyz.jhughes.laundry.fragments.MachineFragment;

/**
 * Created by jeff on 10/4/15.
 */
public class AppSectionsPagerAdapter extends FragmentPagerAdapter {
    private String mRoomName;
    private MachineFragment mFragments[];

    public AppSectionsPagerAdapter(FragmentManager fm, String roomName) {
        super(fm);
        this.mRoomName = roomName;
        this.mFragments = new MachineFragment[2];
    }

    public void setRoomName(String mRoomName) {
        this.mRoomName = mRoomName;
    }

    @Override
    public Fragment getItem(int i) {
        Bundle b = new Bundle();
        if(mFragments[i] == null) {
            mFragments[i] = new MachineFragment();
            b.putString("roomName", mRoomName);
            if(i == 0) {
                //The washers fragment
                b.putBoolean("isDryers", false);
            } else if (i == 1) {
                // The dryers fragment
                b.putBoolean("isDryers", true);
            }
            mFragments[i].setArguments(b);
        }
        return mFragments[i];
    }

    // This is used to allow the view page to refresh when an item is chosen from the drawer
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Washers";
            case 1:
                return "Dryers";
            default:
                return Integer.toString(position);
        }
    }
}
