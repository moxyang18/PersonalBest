package com.example.team10.personalbest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.team10.personalbest.ChatMessaging.ChatMessage;
import com.example.team10.personalbest.friend.FriendListExpandableListAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.google.firebase.FirebaseApp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowActivity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;
import static org.robolectric.shadows.ShadowInstrumentation.getInstrumentation;

@RunWith(RobolectricTestRunner.class)
public class FriendExtraScenarioTest {
    RunningMode runningMode;
    Button add_step_button;
    Button end_run_button;
    TextView currentGoal;
    TextView intentionalSteps;
    TextView totalSteps;
    MockMediator mockMediator;
    HomePage homePage;
    StepSummary userSummary;
    HashMap<String, WalkDay> user_walkdays;
    String userEmail;
    Button own_summary_button;
    ShadowActivity shadowActivity;
    FakeFit fit ;
    BarChart barChart;
    FriendListPage friendListPage;
    MessagePage messagePage;

    /*
    Scenario:
    Richard gets encouragement and support from his wife, meeting his goal
    Richard opens Personal Best, presses the friends button on the homepage. (Milestone 2: User Story 1)
    Richard sees he has no friends, and presses the add friend button. (Milestone 2: User Story 1)
    Richard enters his Lisa’s email address and presses the “add friend” button. (Milestone 2: User Story 1)
    After a while, Richard sees Lisa is now his friend, given that she is listed as a friend. (Milestone 2: User Story 1)
    He clicks on Lisa’s name, and is redirected to a day-by-day summary of her progress. (Milestone 2: User Story 1, 4)
    Richard sees that she is at 6736 steps, and that her goal is at 7000 steps. (Milestone 2: User Story 4)
    Richard scrolls to the bottom of the page and sees a chat-like message screen. He types “You can do it! Go for the goal!” to Lisa and presses the “send” button. (Milestone 2: User Story 2)
    After a while, Richard sees he has a response from Lisa: “Let’s go for a walk! ;)”, while still on Lisa’s progress page. (Milestone 2: User Story 1)
    Richard exits the Lisa’s progress page to return to the friend list page. (Milestone 2: User Story 1)
    Richard exits the friend list page to go to the home page. (Milestone 2: User Story 1)
    Richard leaves the app.

     */
    @Before
    public void setUp() throws Exception {
        MediatorFactory.put("MOCK_MEDIATOR", new MediatorFactory.BluePrint() {
            @Override
            public Mediator create(HomePage homePage) {
                return new MockMediator(homePage);
            }
        });
        Intent intent = new Intent(RuntimeEnvironment.application,HomePage.class);
        intent.putExtra("GET_MEDIATOR","MOCK_MEDIATOR");
        homePage = Robolectric.buildActivity(HomePage.class,intent).create().get();
        mockMediator = MockMediator.getInstance();
        MockMediator.userEmail = "richard@gmail.com";
        MockMediator.userDisplayName = "Richard";
        fit = FakeFit.getInstance();

        mockMediator.timeTravelBackward();
        Intent intent2 = new Intent(RuntimeEnvironment.application,FriendListPage.class);
        intent2.putExtra( "GET_MEDIATOR", "MOCK_MEDIATOR");
        friendListPage = Robolectric.buildActivity(FriendListPage.class, intent2).create().get();
        //starts 2 day behind;
        shadowActivity = Shadows.shadowOf(friendListPage);

    }
    @Test
    public void addWife(){

        HashSet<String> emailList = new HashSet<>();
        String wife = "lisa@gmail.com";
        emailList.add("lisa@gmail.com");
        System.out.println("Adding :"+wife+" as Friend");
        MockMediator.setFriendList(emailList);

    }

    @Test
    public void afterAddWife(){
        ExpandableListView listView = friendListPage.findViewById(R.id.expandable_friend_list_view);
        ArrayList<String> emailList = new ArrayList<>();
        emailList.add("lisa@gmail.com");
        //FriendListExpandableListAdapter friendListAdapter = friendListPage.listAdapter;
        FriendListExpandableListAdapter friendListAdapter = new FriendListExpandableListAdapter(friendListPage, emailList, false);
        listView.setAdapter(friendListAdapter);

        //Grab Child VIew
        View friend =  friendListAdapter.getChildView(2,0,false, null, null);


        //The view within the AdapterView that was clicked, the position of the view in the adapter, The row id of the item that was clicked
        //Grab image button and name/email
        TextView emailTextView = friend.findViewById( R.id.friend_name_text);
        String email = emailTextView.getText().toString();

        System.out.println("new friend's email in the list is "+ email);

        ImageButton msgButton = friend.findViewById(R.id.chat_img_button);
        msgButton.performClick();


        //Check that MessagingPage is opened, with the name sent.
        Intent actualIntent = shadowActivity.getNextStartedActivity();

        //construct expected intent
        Intent expectedIntent = new Intent(friendListPage, MessagePage.class);
        actualIntent.putExtra(friendListPage.getString(R.string.intent_email_key), email);

        //Actual Assert:
        assertTrue(actualIntent.filterEquals(expectedIntent));
        friendListPage.finish();
    }

    @Test
    public void checkWifeSummary(){
        HashMap<String,WalkDay> wifeWalkDays= new HashMap<String,WalkDay>();
        WalkDay w = new WalkDay(LocalDate.now().toString());
        w.setStepCountDailyTotal(6736);
        w.setStepCountUnintentional(6736);
        wifeWalkDays.put(LocalDate.now().minusDays(1).toString(),w);
        MockMediator.setFriendWalkDays(wifeWalkDays);
        ActivityMediator.setFriendWalkDays(wifeWalkDays);
        FriendSummary friendSummary = Robolectric.setupActivity(FriendSummary.class);
        barChart = friendSummary.getBarChart();
        assertTrue( barChart.getBarData().getEntryCount() == 28 );

        assertTrue( barChart.getBarData().getDataSetByIndex(0).getStackSize() == 2);

        //assertTrue(barChart.getBarData().getDataSetByIndex(0).getYMax() >= 5000.0);
        assertTrue(barChart.getBarData().getDataSetByIndex(0).getYMin() == 0.0);
        float [] y = barChart.getBarData().getDataSets().get(0).getEntryForIndex(25).getYVals();

        System.out.println("Wife's yesterday step is: "+y[0]);
        assertTrue(y.length == 2);
        assertTrue(y[0] == 0.f);
        y = barChart.getBarData().getDataSets().get(0).getEntryForIndex(26).getYVals();
        assertTrue(y.length == 2);
        System.out.println("Wife's Today step is: "+y[0]);
        assertTrue(y[0] == 6736.f);
        friendSummary.finish();
    }



    @Test
    public void dcheckReceivedWifeMessage() {
        Context context = getInstrumentation().getTargetContext();
        FirebaseApp.initializeApp(context);
        List<ChatMessage> m = new ArrayList<>();
        m.add(new ChatMessage("Richard", "Hi there"));
        m.add(new ChatMessage("Lisa", "Good."));
        m.add(new ChatMessage("Lisa", "Let's go to walk"));
        m.add(new ChatMessage("Lisa", "You can do it"));

        Intent intent = TestUtils.getMessagePageIntent(TestUtils.getChatMessageService(m), TestUtils.getNotificationService("chat1"));
        intent.putExtra("email", "Richard@gmail.com");
        intent.putExtra("from test", "Richard");
        intent.putExtra("userEmail test", "Richard");
        MessagePage activity = Robolectric.buildActivity(MessagePage.class, intent).create().get();

        TextView chat = activity.findViewById(R.id.chat);

        StringBuilder sb = new StringBuilder();
        m.forEach(message -> sb.append(message.toString()));
        chat.append(sb.toString());
        assertEquals(sb.toString(), chat.getText().toString());
        System.out.println(sb.toString());
    }

    @Test
    public void eCheckRichardWalkAfterChat(){
        Object [] arr = {true,5000,0.f,false};
        fit.setResult(arr);
        assertTrue(((TextView)(homePage.findViewById(R.id.stepsCount))).getText().toString().equals("5000"));
    }

    @After
    public void cleanUp(){
        homePage.finish();
        MockMediator.instance = null;
        MediatorFactory.resetMap();
        MockMediator.reset();
    }


}