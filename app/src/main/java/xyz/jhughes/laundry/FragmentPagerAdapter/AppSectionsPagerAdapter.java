package xyz.jhughes.laundry.FragmentPagerAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import xyz.jhughes.laundry.MachineFragments.DryerFragment;
import xyz.jhughes.laundry.MachineFragments.WasherFragment;

/**
 * Created by jeff on 10/4/15.
 */
public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

    public AppSectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new WasherFragment();
            case 1:
                return new DryerFragment();
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
