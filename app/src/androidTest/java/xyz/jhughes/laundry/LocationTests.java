package xyz.jhughes.laundry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import xyz.jhughes.laundry.activities.LocationActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
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
    public ActivityTestRule<LocationActivity> mLocationActivityRule = new ActivityTestRule<LocationActivity>(LocationActivity.class,true,false);

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

    /* Sanitary Test That The Location Activity Exists */
    @Test
    public void verifyLocationActivityLoads() {
        onView(allOf(withId(R.id.location_activity_toolbar))).check(matches(isDisplayed()));
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.location_activity_toolbar))))
                .check(matches(withText(R.string.title_activity_main)));
        onView(allOf(withId(R.id.location_list_puller))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.recycler_view))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.location_error_text))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.location_error_button),withText(R.string.location_error_try_again))).check(matches(not(isDisplayed())));
    }


}
