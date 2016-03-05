package xyz.jhughes.laundry.analytics;

import android.support.v4.app.Fragment;

/**
 * A way to cleanly track the screen view of a fragment.
 *
 * While legal, probably shouldn't be added to an activity which has screen
 * tracking. Nor should fragments extend this if they don't plan to fill
 * the screen.
 */
public class ScreenTrackedFragment extends Fragment implements ScreenTrackedScreen {
    public String FRAGMENT_NAME;

    @Override
    public void setScreenName(String name) {
        FRAGMENT_NAME = name;
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsHelper.sendScreenViewHit(FRAGMENT_NAME);
    }
}
