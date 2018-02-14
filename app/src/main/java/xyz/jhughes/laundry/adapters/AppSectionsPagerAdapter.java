package xyz.jhughes.laundry.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import xyz.jhughes.laundry.fragments.MachineFragment;

public class AppSectionsPagerAdapter extends FragmentPagerAdapter {
    private String mRoomName;
    private MachineFragment mFragments[];
    public static final int WASHERTABPOSITION = 0;
    public static final int DRYERTABPOSITION = 1;


    public AppSectionsPagerAdapter(FragmentManager fm, String roomName) {
        super(fm);
        this.mRoomName = roomName;
        this.mFragments = new MachineFragment[2];
    }

    public void setRoomName(String mRoomName) {
        this.mRoomName = mRoomName;
    }

    // Do NOT try to save references to the Fragments in getItem(),
    // because getItem() is not always called. If the Fragment
    // was already created then it will be retrieved from the FragmentManger
    // and not here (i.e. getItem() won't be called again).
    @Override
    public Fragment getItem(int i) {
        Bundle b = new Bundle();
        Fragment fragment;
        fragment = new MachineFragment();
        b.putString("roomName", mRoomName);
        if (i == WASHERTABPOSITION) {
            //The washers fragment
            b.putBoolean("isDryers", false);
        } else if (i == DRYERTABPOSITION) {
            // The dryers fragment
            b.putBoolean("isDryers", true);
        }
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        MachineFragment fragment = (MachineFragment) super.instantiateItem(container, position);
        mFragments[position] = fragment;
        return fragment;
    }

    public void notifyFilterChanged() {
        for (MachineFragment m : mFragments) {
            m.updateRecyclerView();
        }
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
            case WASHERTABPOSITION:
                return "Washers";
            case DRYERTABPOSITION:
                return "Dryers";
            default:
                return Integer.toString(position);
        }
    }
}
