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
import xyz.jhughes.laundry.activities.MachineActivity;
import xyz.jhughes.laundry.apiclient.MachineConstants;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static xyz.jhughes.laundry.Matchers.ViewChild.firstChildOf;
import static xyz.jhughes.laundry.Matchers.ViewChild.nthChildOf;

/**
 * Espresso Test Case For The Location Activity
 */
@RunWith(AndroidJUnit4.class)

public class MachineTests {
    String location = "Earhart Hall";

    @Rule
    public ActivityTestRule<MachineActivity> mMachineActivityRule = new ActivityTestRule<MachineActivity>(MachineActivity.class, true, false);

    private MockWebServer mockWebServer;

    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

    @Before
    public void setupMachineActivity() throws Exception {
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
    public void afterMachineActivity() {
        Intents.release();
    }

    private void checkMachineActivity(String roomName) {
        onView(withText(R.string.loading_machines)).perform(pressBack());
        onView(allOf(ViewMatchers.withId(R.id.toolbar))).check(matches(isDisplayed()));
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar))))
                .check(matches(withText(roomName)));
    }

    /* Sanitary Test That The Location Activity Exists */
    @Test
    public void verifyMachineActivityInFirstTimeState() throws Exception {
        String fileName = "earhart_machines.json";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(JSONFileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));
        Intent intent = new Intent();
        intent.putExtra("locationName", location);
        mMachineActivityRule.launchActivity(intent);
        checkMachineActivity(location);

        /* Check if all views under the washers tab are in default states */
        onView(withText("Washers")).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.dryer_list_layout), isDescendantOfA(nthChildOf(withId(R.id.viewpager), 0))))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.dryer_machines_recycler_view), isDescendantOfA(nthChildOf(withId(R.id.viewpager), 0))))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.machine_fragment_too_filtered), isDescendantOfA(nthChildOf(withId(R.id.viewpager), 0))))
                .check(matches(not(isDisplayed())));

        onView(allOf(withId(R.id.machine_fragment_notify_button), isDescendantOfA(nthChildOf(withId(R.id.viewpager), 0))))
                .check(matches(not(isDisplayed())));

        onView(allOf(withId(R.id.machine_activity_filtering_textview), withText("Only showing available machines.")))
                .check(matches(not(isDisplayed())));


        /* Switch Tabs */
        onView(withId(R.id.viewpager)).perform(swipeLeft());

        /* Check if all views under the dryers tab are in default states */
        onView(withText("Dryers")).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.dryer_list_layout), isDescendantOfA(nthChildOf(withId(R.id.viewpager), 1))))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.dryer_machines_recycler_view), isDescendantOfA(nthChildOf(withId(R.id.viewpager), 1))))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.machine_fragment_too_filtered), isDescendantOfA(nthChildOf(withId(R.id.viewpager), 1))))
                .check(matches(not(isDisplayed())));

        onView(allOf(withId(R.id.machine_fragment_notify_button), isDescendantOfA(nthChildOf(withId(R.id.viewpager), 0))))
                .check(matches(not(isDisplayed())));

        onView(allOf(withId(R.id.machine_activity_filtering_textview), withText("Only showing available machines.")))
                .check(matches(not(isDisplayed())));

        /* Check if the first time snackbar is shown */
        onView(allOf(withId(android.support.design.R.id.snackbar_text), withText("Tap a running machine to be notified when it finishes."))).check(matches(isDisplayed()));
    }

    @Test
    public void verifyMachineRecyclerViewItems() throws Exception {

        String fileName = "all_machines_valid.json";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(JSONFileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        Intent intent = new Intent();
        mMachineActivityRule.launchActivity(intent);

        checkMachineActivity(location);
        ViewInteraction locationRecyclerView = onView(allOf(withId(R.id.recycler_view), isDisplayed()));
        String[] locations = Rooms.getRoomsConstantsInstance().getListOfRooms();
        locationRecyclerView.check(new RecyclerViewAdapterNotNullAssertion());
        locationRecyclerView.check(new RecyclerViewItemCountAssertion(locations.length));
    }

    @Test
    public void verifyNoResponseMachineRecyclerViewItems() throws Exception {

        String fileName = "404_response.json";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody(JSONFileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        Intent intent = new Intent();
        mMachineActivityRule.launchActivity(intent);

        checkMachineActivity(location);
        ViewInteraction locationRecyclerView = onView(allOf(withId(R.id.recycler_view), isDisplayed()));
        locationRecyclerView.check(new RecyclerViewAdapterNullAssertion());
    }

    /* Test to check expected offline behavoir */
    @Test
    public void testNetworkOff() throws Exception {

        String fileName = "all_machines_valid.json";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
                .setBody(JSONFileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        Intent intent = new Intent();
        mMachineActivityRule.launchActivity(intent);
        checkMachineActivity(location);
        onView(withId(R.id.location_error_text)).check(matches(isDisplayed()));
        onView(withId(R.id.location_error_button)).check(matches(isDisplayed()));
    }

    /* Test to check if offline then online works */
    @Test
    public void testNetworkOffThenOn() throws Exception {

        String fileName = "all_machines_valid.json";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
                .setBody(JSONFileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        Intent intent = new Intent();
        mMachineActivityRule.launchActivity(intent);
        checkMachineActivity(location);
        onView(withId(R.id.location_error_text)).check(matches(isDisplayed()));
        onView(withId(R.id.location_error_button)).check(matches(isDisplayed()));

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(JSONFileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        onView(withId(R.id.location_error_button)).perform(click());
        ViewInteraction locationRecyclerView = onView(allOf(withId(R.id.recycler_view), isDisplayed()));
        locationRecyclerView.check(new RecyclerViewAdapterNotNullAssertion());
    }

    /* Test to check if all locations loaded */
    @Test
    public void verifyMachinesLoaded() throws Exception {
        String fileName = "all_machines_valid.json";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(JSONFileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        Intent intent = new Intent();
        mMachineActivityRule.launchActivity(intent);
        checkMachineActivity(location);

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
    public void verifyOfflineMachinesLoaded() throws Exception {
        String fileName = "all_machines_offline.json";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(JSONFileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        Intent intent = new Intent();
        mMachineActivityRule.launchActivity(intent);
        checkMachineActivity(location);

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
                .setBody(JSONFileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        Intent intent = new Intent();
        mMachineActivityRule.launchActivity(intent);
        checkMachineActivity(location);

        RecyclerViewMatcher recyclerViewMatcher = withRecyclerView(R.id.recycler_view);
        onView(recyclerViewMatcher.atPosition(0)).perform(click());
        intended(hasComponent(MachineActivity.class.getName()));
    }
}
