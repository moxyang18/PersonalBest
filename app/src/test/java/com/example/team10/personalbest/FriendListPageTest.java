package com.example.team10.personalbest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.team10.personalbest.friend.FriendListExpandableListAdapter;
import com.example.team10.personalbest.friend.TestExpandableListAdapter;

import static org.junit.Assert.*;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowLooper;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.shadows.ShadowInstrumentation.getInstrumentation;

@RunWith(RobolectricTestRunner.class)
public class FriendListPageTest {
    private static final String TAG = "FriendListPageTest :: ";
    FriendListPage activity;
    private ShadowActivity shadowActivity;


    @Before
    public void init() {
        //something
        Log.d(TAG, "Setup for test");

        Intent intent = new Intent(RuntimeEnvironment.application, FriendListPage.class);
        String MEDIATOR_KEY = "GET MEDIATOR";
        MockMediator mockMediator = new MockMediator();
        intent.putExtra( MEDIATOR_KEY, "MOCK_MEDIATOR");
        activity = Robolectric.buildActivity(FriendListPage.class, intent).create().get();

        //activity = Robolectric.setupActivity(FriendListPage.class);
        shadowActivity = Shadows.shadowOf(activity);

        ActivityMediator.userEmail = "";
    }

    /**
     * Test that FriendList HomePage displays friends given that we have friends! :) (6 friends)
     */
    @Test
    public void testFriendListCorrect() {
        //Setup: Make a list of friends to put into FriendList
        ArrayList<String> nameList = new ArrayList<>();
        nameList.add("Kelly");
        nameList.add("Daniel");
        nameList.add("Moxuan");
        nameList.add("Yanzhi");
        nameList.add("Joshua");
        nameList.add("Zhen");

        ExpandableListView listView = activity.findViewById( R.id.expandable_friend_list_view);
        TestExpandableListAdapter testAdapter = new TestExpandableListAdapter(activity, nameList, listView );

        final int NAME_GROUP_INDEX = 2;
        boolean isLastChild = false;
        //grab each child in ExpandableListView, and check if the text matches one of the names the ArrayList of Strings given.
        for( int childPosition = 0; childPosition < testAdapter.getChildrenCount( NAME_GROUP_INDEX ); childPosition += 1 ) {
            if( childPosition == testAdapter.getChildrenCount( NAME_GROUP_INDEX ) - 1 ) {
                isLastChild = true;
            }
            View child = (View)testAdapter.getChildView( NAME_GROUP_INDEX, childPosition, isLastChild, null, null  );
            TextView nameView = child.findViewById(R.id.friend_name_text);
            String name = nameView.getText().toString();
            assertTrue(nameList.contains(name));
        }
    }

    /**
     * Test that FriendList HomePage displays no friends given that we have no friends! :) (0 friends)
     */
    @Test
    public void testNoFriendOnUI() {
        ArrayList<String> friendList = new ArrayList<>();
        ExpandableListView listView = activity.findViewById( R.id.expandable_friend_list_view);
        TestExpandableListAdapter testAdapter = new TestExpandableListAdapter(activity, friendList, listView );

        final int NAME_GROUP_INDEX = 2;
        assertTrue( testAdapter.getChildrenCount(NAME_GROUP_INDEX) == 0 );

    }

    /**
     * Tests Back button returns to Home Page
     */
    @Test
    public void testBackButton() {
        //Grab Back Button
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        Menu menu = toolbar.getMenu();
        MenuItem backButton = toolbar.getMenu().findItem( android.R.id.home );

        //backButton.performClick();
        shadowActivity.clickMenuItem( android.R.id.home );
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        assertTrue(shadowActivity.isFinishing());
    }

    /**
     * Tests pressing Chat Button goes to Messaging Page
     */
    @Test
    public void testMessageButton() {
        //Grab the listView and the list of names.
        ExpandableListView listView = activity.findViewById(R.id.expandable_friend_list_view);
        ArrayList<String> emailList = new ArrayList<>();
        //emailList.addAll(sharedPreferences.getStringSet(emailKey, new HashSet<>()));

        emailList.add("Frind1");
        emailList.add("friend2"); //TODO check why shared pref does not work

        FriendListExpandableListAdapter friendListAdapter = new FriendListExpandableListAdapter(activity, emailList, false);
        listView.setAdapter(friendListAdapter);

        //Grab Child VIew
        View friend =  friendListAdapter.getChildView(2,1,false, null, null);


        //The view within the AdapterView that was clicked, the position of the view in the adapter, The row id of the item that was clicked
        //Grab image button and name/email
        TextView emailTextView = friend.findViewById( R.id.friend_name_text);
        String email = emailTextView.getText().toString();

        ImageButton msgButton = friend.findViewById(R.id.chat_img_button);
        msgButton.performClick();


        //Check that MessagingPage is opened, with the name sent.
        Intent actualIntent = shadowActivity.getNextStartedActivity();

        //construct expected intent
        Intent expectedIntent = new Intent(activity, MessagePage.class);
        actualIntent.putExtra(activity.getString(R.string.intent_email_key), email);

        //Actual Assert:
        assertTrue(actualIntent.filterEquals(expectedIntent));
    }

    /**
     * Tests pressing the Friend's name goes to FriendHomePage TODO
     */
    @Test
    public void testGoToFriendSummaryPage() {
        //Grab the listView and the list of names.
        ExpandableListView listView = activity.findViewById(R.id.expandable_friend_list_view);
        ArrayList<String> emailList = new ArrayList<>();
        emailList.add("Friend1");
        emailList.add("friend2");

        TestExpandableListAdapter testAdapter = new TestExpandableListAdapter(activity, emailList, listView);

        //Grab Child View
        View friend =  testAdapter.getChildView(2,1,true, null, null);

        //Click the childview that corresponds to friend
        friend.setClickable(true);
        testAdapter.click(2, 1, false, null, null );

        //Grab button and name/email
        TextView emailTextView = friend.findViewById( R.id.friend_name_text);
        String email = emailTextView.getText().toString();
        activity.launchFriendHomePage(listView, 2, 1);

        Intent actualIntent = shadowActivity.getNextStartedActivity();
        assertNotNull(actualIntent);


        //construct expected intent
        Intent expectedIntent = new Intent(activity, FriendSummary.class); //TODO replace with Moxuan's class activity later
        actualIntent.putExtra(activity.getString(R.string.intent_email_key), email);

        //Actual Assert:
        assertTrue(actualIntent.filterEquals(expectedIntent));
    }
}
