package xyz.jhughes.laundry.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import xyz.jhughes.laundry.LaundryParser.Constants;
import xyz.jhughes.laundry.LaundryParser.Rooms;
import xyz.jhughes.laundry.Matchers.RecyclerViewItemCountAssertion;
import xyz.jhughes.laundry.Matchers.RecyclerViewMatcher;
import xyz.jhughes.laundry.R;
import xyz.jhughes.laundry.activities.LocationActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;

/**
 * Espresso Test Case For The Location Activity
 */
@RunWith(AndroidJUnit4.class)

public class LocationTests {
    @Rule
    public ActivityTestRule<LocationActivity> mLocationActivityRule = new ActivityTestRule<LocationActivity>(LocationActivity.class, true, false);

    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

    @Before
    public void setupLocationActivity() {
        SharedPreferences prefs =
                InstrumentationRegistry.getTargetContext().getSharedPreferences("xyz.jhughes.laundry", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
        Intents.init();
        Intent intent = new Intent();
        mLocationActivityRule.launchActivity(intent);
    }

    @After
    public void afterLocationActivity() {
        Intents.release();
    }

    private void checkLocationActivity() {
        onView(allOf(ViewMatchers.withId(R.id.location_activity_toolbar))).check(matches(isDisplayed()));
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.location_activity_toolbar))))
                .check(matches(withText(R.string.title_activity_main)));
    }

    /* Sanitary Test That The Location Activity Exists */
    @Test
    public void verifyLocationActivityLoads() {
        checkLocationActivity();
        onView(allOf(withId(R.id.location_list_puller))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.recycler_view))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.location_error_text))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.location_error_button), withText(R.string.location_error_try_again))).check(matches(not(isDisplayed())));
    }

    @Test
    public void verifyLocationRecyclerViewItems() {
        checkLocationActivity();
        ViewInteraction locationRecyclerView = onView(allOf(withId(R.id.recycler_view),isDisplayed()));
        String[] locations = Rooms.getRoomsConstantsInstance().getListOfRooms();
        locationRecyclerView.check(new RecyclerViewItemCountAssertion(locations.length));
    }

    @Test
    public void verifyLocationRecyclerViewItemLoaded() {
        checkLocationActivity();
        RecyclerViewMatcher recyclerViewMatcher = withRecyclerView(R.id.recycler_view);
        recyclerViewMatcher.atPositionOnView(0,R.id.image_view_location).matches(isDisplayed());
        recyclerViewMatcher.atPositionOnView(0,R.id.text_view_location_name).matches(isDisplayed());
        recyclerViewMatcher.atPositionOnView(0,R.id.text_view_offline).matches(not(isDisplayed()));
        recyclerViewMatcher.atPositionOnView(0,R.id.text_view_washer).matches(isDisplayed());
        recyclerViewMatcher.atPositionOnView(0,R.id.text_view_washer_count).matches(isDisplayed());
        recyclerViewMatcher.atPositionOnView(0,R.id.text_view_washer_total).matches(isDisplayed());
        recyclerViewMatcher.atPositionOnView(0,R.id.text_view_dryer).matches(isDisplayed());
        recyclerViewMatcher.atPositionOnView(0,R.id.text_view_dryer_count).matches(isDisplayed());
        recyclerViewMatcher.atPositionOnView(0,R.id.text_view_dryer_total).matches(isDisplayed());
    }
}
