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
import xyz.jhughes.laundry.Matchers.RecyclerViewItemCountAssertion;
import xyz.jhughes.laundry.Matchers.RecyclerViewMatcher;
import xyz.jhughes.laundry.R;
import xyz.jhughes.laundry.activities.MachineActivity;
import xyz.jhughes.laundry.apiclient.MachineConstants;
import xyz.jhughes.laundry.laundryparser.MachineStates;
import xyz.jhughes.laundry.laundryparser.Rooms;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static xyz.jhughes.laundry.Matchers.ViewChild.nthChildOf;

/**
 * Espresso Test Case For The Location Activity
 */
@RunWith(AndroidJUnit4.class)
public class MachineTests {
    String location = "Earhart Hall";

    @Rule
    public ActivityTestRule<MachineActivity> machineActivityRule = new ActivityTestRule<MachineActivity>(MachineActivity.class, true, false);

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

    private void checkLocationActivity() {
        onView(allOf(withId(R.id.toolbar))).check(matches(isDisplayed()));
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar))))
                .check(matches(withText(R.string.title_activity_main)));
    }

    private void checkMachineActivity(String roomName) {
        onView(allOf(withId(R.id.toolbar))).check(matches(isDisplayed()));
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar))))
                .check(matches(withText(roomName)));
    }

    /* Sanitary Test That The Location Activity Exists */
    @Test
    public void verifyMachineActivityInFirstTimeState() throws Exception {
        String fileName = "earhart_machines.json";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(FileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));
        Intent intent = new Intent();
        intent.putExtra("locationName", location);
        machineActivityRule.launchActivity(intent);

        onView(withText(R.string.loading_machines)).perform(pressBack());

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

        /*TODO: Check if the first time snackbar is shown */
        //onView(allOf(withId(android.supportR.id.snackbar_text), withText("Tap a running machine to be notified when it finishes."))).check(matches(isDisplayed()));
    }

    @Test
    public void verifyMachineRecyclerViewItems() throws Exception {

        String fileName = "earhart_machines.json";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(FileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));
        Intent intent = new Intent();
        intent.putExtra("locationName", location);
        machineActivityRule.launchActivity(intent);

        onView(withText(R.string.loading_machines)).perform(pressBack());

        checkMachineActivity(location);

        /*Check if washer recycler view has 16 dryers */
        onView(withText("Washers")).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.dryer_list_layout), isDescendantOfA(nthChildOf(withId(R.id.viewpager), 0))))
                .check(matches(isDisplayed()));

        ViewInteraction locationRecyclerView = onView(allOf(withId(R.id.dryer_machines_recycler_view), isDescendantOfA(nthChildOf(withId(R.id.viewpager), 0)), isDisplayed()));
        String[] locations = Rooms.getRoomsConstantsInstance().getListOfRooms();
        locationRecyclerView.check(new RecyclerViewAdapterNotNullAssertion());
        locationRecyclerView.check(new RecyclerViewItemCountAssertion(16));

        /* Switch Tabs */
        onView(withId(R.id.viewpager)).perform(swipeLeft());

        /* Check if dryer recycler view has 16 dryers */
        onView(withText("Dryers")).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.dryer_list_layout), isDescendantOfA(nthChildOf(withId(R.id.viewpager), 1))))
                .check(matches(isDisplayed()));

        locationRecyclerView = onView(allOf(withId(R.id.dryer_machines_recycler_view), isDescendantOfA(nthChildOf(withId(R.id.viewpager), 0)), isDisplayed()));
        locationRecyclerView.check(new RecyclerViewAdapterNotNullAssertion());
        locationRecyclerView.check(new RecyclerViewItemCountAssertion(16));
    }

    @Test
    public void verifyNoResponseMachineRecyclerViewItems() throws Exception {

        String fileName = "404_response.json";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody(FileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        Intent intent = new Intent();
        intent.putExtra("locationName", location);
        machineActivityRule.launchActivity(intent);
        onView(allOf(withText(R.string.error_client_message), isDisplayed()));

    }

    /* Test to check expected offline behavior */
    @Test
    public void testNetworkOff() throws Exception {

        String fileName = "earhart_machines.json";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
                .setBody(FileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        Intent intent = new Intent();
        intent.putExtra("locationName", location);
        machineActivityRule.launchActivity(intent);
        onView(allOf(withText(R.string.error_server_message), isDisplayed()));
    }

    /* Test to check if offline then online works */
    @Test
    public void testNetworkOffThenOn() throws Exception {

        String fileName = "earhart_machines.json";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_UNAVAILABLE)
                .setBody(FileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        Intent intent = new Intent();
        intent.putExtra("locationName", location);
        machineActivityRule.launchActivity(intent);
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(FileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));
        onView(allOf(withText(R.string.error_server_message), isDisplayed()));
        onView(allOf(withText("Okay"), isDisplayed())).perform(click());

        onView(withId(R.id.location_error_text)).check(matches(isDisplayed()));
        onView(withId(R.id.location_error_button)).check(matches(isDisplayed()));

        fileName = "all_machines_valid.json";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(FileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        onView(withId(R.id.location_error_button)).perform(click());
        checkLocationActivity();

        fileName = "earhart_machines.json";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(FileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));

        ViewInteraction locationRecyclerView = onView(allOf(withId(R.id.recycler_view), isDisplayed()));
        locationRecyclerView.check(new RecyclerViewAdapterNotNullAssertion());

//        ViewInteraction earhartCard = onView(allOf(isDescendantOfA(withId(R.id.recycler_view)), withId(R.id.card_view), hasDescendant(withText("Earhart Hall"))));
//        earhartCard.perform(click());

//        checkLocationActivity();

//        onView(withText(R.string.loading_machines)).perform(pressBack());
//        checkMachineActivity(location);
    }

    /* Test To Verify All States Are Displaying Under Washers */
    @Test
    public void testAllMachineStates() throws Exception {
        String fileName = "earhart_machines_custom_states.json";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(FileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));
        Intent intent = new Intent();
        intent.putExtra("locationName", location);
        machineActivityRule.launchActivity(intent);

        onView(withText(R.string.loading_machines)).perform(pressBack());

        checkMachineActivity(location);

        /*Check if washer recycler view has 16 dryers */
        onView(withText("Washers")).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.dryer_list_layout), isDescendantOfA(nthChildOf(withId(R.id.viewpager), 0))))
                .check(matches(isDisplayed()));

        ViewInteraction locationRecyclerView = onView(allOf(withId(R.id.dryer_machines_recycler_view), isDescendantOfA(nthChildOf(withId(R.id.viewpager), 0)), isDisplayed()));
        locationRecyclerView.check(new RecyclerViewAdapterNotNullAssertion());
        locationRecyclerView.check(new RecyclerViewItemCountAssertion(7));

        RecyclerViewMatcher washerRecyclerViewMatcher = withRecyclerView(R.id.dryer_machines_recycler_view);
        washerRecyclerViewMatcher.atPositionOnView(0, R.id.machine_status_text_view).matches(allOf(withText(MachineStates.AVAILABLE), isDisplayed()));
        washerRecyclerViewMatcher.atPositionOnView(1, R.id.machine_status_text_view).matches(allOf(withText(MachineStates.IN_USE), isDisplayed()));
        washerRecyclerViewMatcher.atPositionOnView(2, R.id.machine_status_text_view).matches(allOf(withText(MachineStates.ALMOST_DONE), isDisplayed()));
        washerRecyclerViewMatcher.atPositionOnView(3, R.id.machine_status_text_view).matches(allOf(withText(MachineStates.END_CYCLE), isDisplayed()));
        washerRecyclerViewMatcher.atPositionOnView(4, R.id.machine_status_text_view).matches(allOf(withText(MachineStates.READY), isDisplayed()));
        washerRecyclerViewMatcher.atPositionOnView(5, R.id.machine_status_text_view).matches(allOf(withText(MachineStates.NOT_ONLINE), isDisplayed()));
        washerRecyclerViewMatcher.atPositionOnView(6, R.id.machine_status_text_view).matches(allOf(withText(MachineStates.OUT_OF_ORDER), isDisplayed()));

        /* Switch Tabs */
        onView(withId(R.id.viewpager)).perform(swipeLeft());

        /* Check if dryer recycler view has 16 dryers */
        onView(withText("Dryers")).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.dryer_list_layout), isDescendantOfA(nthChildOf(withId(R.id.viewpager), 1))))
                .check(matches(isDisplayed()));

        locationRecyclerView = onView(allOf(withId(R.id.dryer_machines_recycler_view), isDescendantOfA(nthChildOf(withId(R.id.viewpager), 0)), isDisplayed()));
        locationRecyclerView.check(new RecyclerViewAdapterNotNullAssertion());
        locationRecyclerView.check(new RecyclerViewItemCountAssertion(7));

        RecyclerViewMatcher dryerRecyclerViewMatcher = withRecyclerView(R.id.dryer_machines_recycler_view);
        dryerRecyclerViewMatcher.atPositionOnView(0, R.id.machine_status_text_view).matches(allOf(withText(MachineStates.AVAILABLE), isDisplayed()));
        dryerRecyclerViewMatcher.atPositionOnView(1, R.id.machine_status_text_view).matches(allOf(withText(MachineStates.IN_USE), isDisplayed()));
        dryerRecyclerViewMatcher.atPositionOnView(2, R.id.machine_status_text_view).matches(allOf(withText(MachineStates.ALMOST_DONE), isDisplayed()));
        dryerRecyclerViewMatcher.atPositionOnView(3, R.id.machine_status_text_view).matches(allOf(withText(MachineStates.END_CYCLE), isDisplayed()));
        dryerRecyclerViewMatcher.atPositionOnView(4, R.id.machine_status_text_view).matches(allOf(withText(MachineStates.READY), isDisplayed()));
        dryerRecyclerViewMatcher.atPositionOnView(5, R.id.machine_status_text_view).matches(allOf(withText(MachineStates.NOT_ONLINE), isDisplayed()));
        dryerRecyclerViewMatcher.atPositionOnView(6, R.id.machine_status_text_view).matches(allOf(withText(MachineStates.OUT_OF_ORDER), isDisplayed()));
    }

    /* Test that verifies if alarm tutorials appear */
    @Test
    public void testAlarmTutorial() throws Exception {
        String fileName = "earhart_machines.json";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(FileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));
        Intent intent = new Intent();
        intent.putExtra("locationName", location);
        machineActivityRule.launchActivity(intent);

        onView(withText(R.string.loading_machines)).perform(pressBack());

        checkMachineActivity(location);


        /*Check if dryer recycler view has 16 dryers */
        onView(withText("Washers")).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.dryer_list_layout), isDescendantOfA(nthChildOf(withId(R.id.viewpager), 0))))
                .check(matches(isDisplayed()));

        ViewInteraction locationRecyclerView = onView(allOf(withId(R.id.dryer_machines_recycler_view), isDescendantOfA(nthChildOf(withId(R.id.viewpager), 0)), isDisplayed()));
        locationRecyclerView.check(new RecyclerViewAdapterNotNullAssertion());
        locationRecyclerView.check(new RecyclerViewItemCountAssertion(16));

        ViewInteraction inProgressWasher = onView(allOf(isDescendantOfA(withId(R.id.card_view)), withText("Washer 12")));
        inProgressWasher.check(matches(isDisplayed()));
        inProgressWasher.perform(click());

        onView(allOf(withText("Load Washer 12 to start a timer for when the machine is finished"),isDisplayed()));
        onView(withText(R.string.alarm)).check(matches(isDisplayed()));
        onView(withText(R.string.available_timer_refresh)).check(matches(isDisplayed()));
        onView(withText(R.string.available_timer_cancel)).check(matches(isDisplayed()));
        onView(withText(R.string.available_timer_refresh)).perform(click());
        onView(withText(R.string.available_timer_cancel)).perform(click());
        onView(allOf(withText("Load Washer 12 to start a timer for when the machine is finished"), not(isDisplayed())));
    }

    /* Test that verifies if alarm tutorials appear */
    @Test
    public void testAlarmDialog() throws Exception {
        String fileName = "earhart_machines.json";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(FileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));
        Intent intent = new Intent();
        intent.putExtra("locationName", location);
        machineActivityRule.launchActivity(intent);

        onView(withText(R.string.loading_machines)).perform(pressBack());

        checkMachineActivity(location);


        /*Check if dryer recycler view has 16 dryers */
        onView(withText("Washers")).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.dryer_list_layout), isDescendantOfA(nthChildOf(withId(R.id.viewpager), 0))))
                .check(matches(isDisplayed()));

        ViewInteraction locationRecyclerView = onView(allOf(withId(R.id.dryer_machines_recycler_view), isDescendantOfA(nthChildOf(withId(R.id.viewpager), 0)), isDisplayed()));
        locationRecyclerView.check(new RecyclerViewAdapterNotNullAssertion());
        locationRecyclerView.check(new RecyclerViewItemCountAssertion(16));

        ViewInteraction inProgressWasher = onView(allOf(withId(R.id.card_view), hasDescendant(withText("Washer 6"))));
        inProgressWasher.check(matches(isDisplayed()));
        inProgressWasher.perform(click());

        onView(withText(R.string.alarm)).check(matches(isDisplayed()));
        onView(withText(R.string.ask_set_alarm)).check(matches(isDisplayed()));
        onView(withText(R.string.yes)).check(matches(isDisplayed()));
        onView(withText(R.string.no)).perform(click());
        onView(allOf(withText(R.string.ask_set_alarm), not(isDisplayed())));

        inProgressWasher.perform(click());

        onView(withText(R.string.alarm)).check(matches(isDisplayed()));
        onView(withText(R.string.ask_set_alarm)).check(matches(isDisplayed()));
        onView(withText(R.string.no)).check(matches(isDisplayed()));
        onView(withText(R.string.yes)).perform(click());
        onView(allOf(withText(R.string.ask_set_alarm), not(isDisplayed())));
    }

    /* Test to verify show available machines only works */
    @Test
    public void testFilters() throws Exception {
        String fileName = "earhart_machines_timer_start.json";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(FileExtracter.getStringFromFile(InstrumentationRegistry.getContext(), fileName)));
        Intent intent = new Intent();
        intent.putExtra("locationName", location);
        machineActivityRule.launchActivity(intent);

        onView(withText(R.string.loading_machines)).perform(pressBack());

        checkMachineActivity(location);


        /*Check if dryer recycler view has 16 dryers */
        onView(withText("Washers")).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.dryer_list_layout), isDescendantOfA(nthChildOf(withId(R.id.viewpager), 0))))
                .check(matches(isDisplayed()));

        ViewInteraction locationRecyclerView = onView(allOf(withId(R.id.dryer_machines_recycler_view), isDescendantOfA(nthChildOf(withId(R.id.viewpager), 0)), isDisplayed()));
        locationRecyclerView.check(new RecyclerViewAdapterNotNullAssertion());
        locationRecyclerView.check(new RecyclerViewItemCountAssertion(16));

        onView(withId(R.id.display_parameters)).perform(click());
        onView(withId(R.id.filter_dialog_switch)).perform(click());
        onView(withText(R.string.ok)).perform(click());
        locationRecyclerView.check(new RecyclerViewItemCountAssertion(15));
    }
}
