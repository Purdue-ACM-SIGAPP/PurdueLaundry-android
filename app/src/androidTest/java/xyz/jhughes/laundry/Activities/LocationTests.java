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

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;

import xyz.jhughes.laundry.JSONFileExtracter;
import xyz.jhughes.laundry.LaundryParser.Rooms;
import xyz.jhughes.laundry.Matchers.RecyclerViewAdapterNotNullAssertion;
import xyz.jhughes.laundry.Matchers.RecyclerViewAdapterNullAssertion;
import xyz.jhughes.laundry.Matchers.RecyclerViewItemCountAssertion;
import xyz.jhughes.laundry.Matchers.RecyclerViewMatcher;
import xyz.jhughes.laundry.R;
import xyz.jhughes.laundry.activities.LocationActivity;
import xyz.jhughes.laundry.activities.MachineActivity;
import xyz.jhughes.laundry.apiclient.MachineConstants;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
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

    private MockWebServer mockWebServer;

    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

    @Before
    public void setupLocationActivity() throws Exception{
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        MachineConstants.API_ROOT = mockWebServer.url("/").toString();

        SharedPreferences prefs =
                InstrumentationRegistry.getTargetContext().getSharedPreferences("xyz.jhughes.laundry", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
        Intents.init();
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
        Intent intent = new Intent();
        mLocationActivityRule.launchActivity(intent);
        checkLocationActivity();
        onView(allOf(withId(R.id.location_list_puller))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.recycler_view))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.location_error_text))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.location_error_button), withText(R.string.location_error_try_again))).check(matches(not(isDisplayed())));
    }

    @Test
    public void verifyLocationRecyclerViewItems() throws Exception{

        String fileName = "all_machine_valid.json";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(JSONFileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        Intent intent = new Intent();
        mLocationActivityRule.launchActivity(intent);

        checkLocationActivity();
        ViewInteraction locationRecyclerView = onView(allOf(withId(R.id.recycler_view),isDisplayed()));
        String[] locations = Rooms.getRoomsConstantsInstance().getListOfRooms();
        locationRecyclerView.check(new RecyclerViewAdapterNotNullAssertion());
        locationRecyclerView.check(new RecyclerViewItemCountAssertion(locations.length));
    }

    @Test
    public void verifyNoResponseLocationRecyclerViewItems() throws Exception{

        String fileName = "404_response.json";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody(JSONFileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        Intent intent = new Intent();
        mLocationActivityRule.launchActivity(intent);

        checkLocationActivity();
        ViewInteraction locationRecyclerView = onView(allOf(withId(R.id.recycler_view),isDisplayed()));
        locationRecyclerView.check(new RecyclerViewAdapterNullAssertion());
    }

    /* Test to check expected offline behavoir */
    @Test
    public void testNetworkOff() throws Exception{

        String fileName = "all_machine_valid.json";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
                .setBody(JSONFileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        Intent intent = new Intent();
        mLocationActivityRule.launchActivity(intent);
        checkLocationActivity();
        onView(withId(R.id.location_error_text)).check(matches(isDisplayed()));
        onView(withId(R.id.location_error_button)).check(matches(isDisplayed()));
    }

    /* Test to check if offline then online works */
    @Test
    public void testNetworkOffThenOn() throws Exception{

        String fileName = "all_machine_valid.json";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
                .setBody(JSONFileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        Intent intent = new Intent();
        mLocationActivityRule.launchActivity(intent);
        checkLocationActivity();
        onView(withId(R.id.location_error_text)).check(matches(isDisplayed()));
        onView(withId(R.id.location_error_button)).check(matches(isDisplayed()));

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(JSONFileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        onView(withId(R.id.location_error_button)).perform(click());
        ViewInteraction locationRecyclerView = onView(allOf(withId(R.id.recycler_view),isDisplayed()));
        locationRecyclerView.check(new RecyclerViewAdapterNotNullAssertion());
    }

    /* Test to check if all locations loaded */
    @Test
    public void verifyLocationRecyclerViewItemLoaded() throws Exception {
        String fileName = "all_machine_valid.json";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(JSONFileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        Intent intent = new Intent();
        mLocationActivityRule.launchActivity(intent);
        checkLocationActivity();

        RecyclerViewMatcher recyclerViewMatcher = withRecyclerView(R.id.recycler_view);

        String[] locations = Rooms.getRoomsConstantsInstance().getListOfRooms();

        for (int i = 0; i < locations.length; i++) {
            recyclerViewMatcher.atPositionOnView(i, R.id.image_view_location).matches(isDisplayed());
            recyclerViewMatcher.atPositionOnView(i, R.id.text_view_location_name).matches(allOf(isDisplayed(), withText(locations[i])));
            recyclerViewMatcher.atPositionOnView(i, R.id.text_view_offline).matches(not(isDisplayed()));
            recyclerViewMatcher.atPositionOnView(i, R.id.text_view_washer).matches(isDisplayed());
            recyclerViewMatcher.atPositionOnView(i, R.id.text_view_washer_count).matches(isDisplayed());
            recyclerViewMatcher.atPositionOnView(i, R.id.text_view_washer_total).matches(isDisplayed());
            recyclerViewMatcher.atPositionOnView(i, R.id.text_view_dryer).matches(isDisplayed());
            recyclerViewMatcher.atPositionOnView(i, R.id.text_view_dryer_count).matches(isDisplayed());
            recyclerViewMatcher.atPositionOnView(i, R.id.text_view_dryer_total).matches(isDisplayed());
        }
    }

    /* Test to see if launching a machine activity works */
    @Test
    public void verifyLaunchMachineActivity() throws Exception {
        String fileName = "all_machine_valid.json";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(JSONFileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        Intent intent = new Intent();
        mLocationActivityRule.launchActivity(intent);
        checkLocationActivity();

        RecyclerViewMatcher recyclerViewMatcher = withRecyclerView(R.id.recycler_view);
        onView(recyclerViewMatcher.atPosition(0)).perform(click());
        intended(hasComponent(MachineActivity.class.getName()));
    }
}