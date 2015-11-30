package xyz.jhughes.laundry.FragmentPagerAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import xyz.jhughes.laundry.MachineFragments.MachineFragment;

/**
 * Created by jeff on 10/4/15.
 */
public class AppSectionsPagerAdapter extends FragmentPagerAdapter {
    private String selected;

    public AppSectionsPagerAdapter(FragmentManager fm, String selected) {
        super(fm);
        this.selected = selected;
    }

    public void setSelected(String selected){
        this.selected = selected;
    }

    @Override
    public Fragment getItem(int i) {
        MachineFragment fragment = new MachineFragment();
        Bundle b = new Bundle();
        switch (i) {
            case 0:
                b.putBoolean("isDryers",false);
                fragment.setArguments(b);
                return fragment;
            case 1:
                b.putBoolean("isDryers",true);
                fragment.setArguments(b);
                return fragment;
            default:
                return null;
        }
    }

    // This is used to allow the view page to refresh when an item is chosen from the drawer
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return 2;
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
