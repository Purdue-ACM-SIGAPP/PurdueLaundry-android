package xyz.jhughes.laundry.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;

import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import xyz.jhughes.laundry.FileExtracter;
import xyz.jhughes.laundry.Matchers.RecyclerViewAdapterNotNullAssertion;
import xyz.jhughes.laundry.Matchers.RecyclerViewAdapterNullAssertion;
import xyz.jhughes.laundry.Matchers.RecyclerViewItemCountAssertion;
import xyz.jhughes.laundry.Matchers.RecyclerViewMatcher;
import xyz.jhughes.laundry.R;
import xyz.jhughes.laundry.views.activities.LocationActivity;
import xyz.jhughes.laundry.views.activities.MachineActivity;
import xyz.jhughes.laundry.data.MachineConstants;
import xyz.jhughes.laundry.laundryparser.Rooms;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;

/**
 * Espresso Test Case For The Location Activity
 */
@RunWith(AndroidJUnit4.class)

public class LocationTests {
    @Rule
    public ActivityTestRule<LocationActivity> locationActivityRule = new ActivityTestRule<LocationActivity>(LocationActivity.class, true, false);

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
        onView(allOf(withId(R.id.toolbar))).check(matches(isDisplayed()));
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar))))
                .check(matches(withText(R.string.title_activity_main)));
    }

    /* Sanitary Test That The Location Activity Exists */
    @Test
    public void verifyLocationActivityLoads() {
        Intent intent = new Intent();
        locationActivityRule.launchActivity(intent);
        checkLocationActivity();
        onView(allOf(withId(R.id.location_list_puller))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.recycler_view))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.location_error_text))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.location_error_button), withText(R.string.location_error_try_again))).check(matches(not(isDisplayed())));
    }

    @Test
    public void verifyLocationRecyclerViewItems() throws Exception{

        String fileName = "all_machines_valid.json";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(FileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        Intent intent = new Intent();
        locationActivityRule.launchActivity(intent);

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
                .setBody(FileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        Intent intent = new Intent();
        locationActivityRule.launchActivity(intent);

        checkLocationActivity();
        ViewInteraction locationRecyclerView = onView(allOf(withId(R.id.recycler_view),isDisplayed()));
        locationRecyclerView.check(new RecyclerViewAdapterNullAssertion());
    }

    /* Test to check expected offline behavoir */
    @Test
    public void testNetworkOff() throws Exception{

        String fileName = "all_machines_valid.json";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
                .setBody(FileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        Intent intent = new Intent();
        locationActivityRule.launchActivity(intent);
        checkLocationActivity();
        onView(withId(R.id.location_error_text)).check(matches(isDisplayed()));
        onView(withId(R.id.location_error_button)).check(matches(isDisplayed()));
    }

    /* Test to check if offline then online works */
    @Test
    public void testNetworkOffThenOn() throws Exception{

        String fileName = "all_machines_valid.json";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
                .setBody(FileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        Intent intent = new Intent();
        locationActivityRule.launchActivity(intent);
        checkLocationActivity();
        onView(withId(R.id.location_error_text)).check(matches(isDisplayed()));
        onView(withId(R.id.location_error_button)).check(matches(isDisplayed()));

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(FileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        onView(withId(R.id.location_error_button)).perform(click());
        ViewInteraction locationRecyclerView = onView(allOf(withId(R.id.recycler_view),isDisplayed()));
        locationRecyclerView.check(new RecyclerViewAdapterNotNullAssertion());
    }

    /* Test to check if all locations loaded */
    @Test
    public void verifyLocationsLoaded() throws Exception {
        String fileName = "all_machines_valid.json";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(FileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        Intent intent = new Intent();
        locationActivityRule.launchActivity(intent);
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

    /* Test to check if all locations are offline */
    @Test
    public void verifyOfflineLocationsLoaded() throws Exception {
        String fileName = "all_machines_offline.json";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(FileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        Intent intent = new Intent();
        locationActivityRule.launchActivity(intent);
        checkLocationActivity();

        RecyclerViewMatcher recyclerViewMatcher = withRecyclerView(R.id.recycler_view);

        String[] locations = Rooms.getRoomsConstantsInstance().getListOfRooms();

        for (int i = 0; i < locations.length; i++) {
            recyclerViewMatcher.atPositionOnView(i, R.id.image_view_location).matches(isDisplayed());
            recyclerViewMatcher.atPositionOnView(i, R.id.text_view_location_name).matches(allOf(isDisplayed(), withText(locations[i])));
            recyclerViewMatcher.atPositionOnView(i, R.id.text_view_offline).matches(isDisplayed());
            recyclerViewMatcher.atPositionOnView(i, R.id.text_view_washer).matches(not(isDisplayed()));
            recyclerViewMatcher.atPositionOnView(i, R.id.text_view_washer_count).matches(not(isDisplayed()));
            recyclerViewMatcher.atPositionOnView(i, R.id.text_view_washer_total).matches(not(isDisplayed()));
            recyclerViewMatcher.atPositionOnView(i, R.id.text_view_dryer).matches(not(isDisplayed()));
            recyclerViewMatcher.atPositionOnView(i, R.id.text_view_dryer_count).matches(not(isDisplayed()));
            recyclerViewMatcher.atPositionOnView(i, R.id.text_view_dryer_total).matches(not(isDisplayed()));
        }
    }

    /* Test to see if launching a machine activity works */
    @Test
    public void verifyLaunchMachineActivity() throws Exception {
        String fileName = "all_machines_valid.json";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(FileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        Intent intent = new Intent();
        locationActivityRule.launchActivity(intent);
        checkLocationActivity();

        RecyclerViewMatcher recyclerViewMatcher = withRecyclerView(R.id.recycler_view);
        onView(recyclerViewMatcher.atPosition(0)).perform(click());
        intended(hasComponent(MachineActivity.class.getName()));
    }
}
