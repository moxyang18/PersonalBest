package com.example.team10.personalbest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;

import com.example.team10.personalbest.friend.FriendListExpandableListAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FriendListPage extends AppCompatActivity {
    private String TAG = "FriendListPage:";

    final int FRIEND_INDEX = 2;

    ExpandableListView friendExpandableList;
    FriendListExpandableListAdapter listAdapter;
    //TODO use this instead, obtained from ActivityMediator
    private static HashSet<String> friendList = new HashSet<String>();
    Mediator activityMediator;
    String MEDIATOR_KEY = "GET MEDIATOR";
    String myEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //TODO use this instead, obtained from ActivityMediator

        Intent intent = getIntent();
        String MediatorKey =null;

        if(intent!= null)
            MediatorKey = intent.getStringExtra(MEDIATOR_KEY);

        if(MediatorKey == null || MediatorKey.equals("ACTIVITY_MEDIATOR")){
            activityMediator = ActivityMediator.getInstance();
        }
        else if (MediatorKey.equals("MOCK_MEDIATOR")){
            activityMediator = MockMediator.getInstance();
        }else{
            Log.d(TAG, "ERROR, WRONG KEY FROM INTENT");
        }
        friendList = activityMediator.getFriendListByI();


        Log.d(TAG, "loading friendlistpage, current list is"+friendList.toString());
        for(String s:friendList){
            Log.d(TAG,"have user "+s+" inside friendlist before loading page");
        }
        /**
        GoogleSignInAccount user = GoogleSignIn.getLastSignedInAccount(this);
         */

        myEmail = activityMediator.getUserEmail();


        ArrayList<String> emailList = new ArrayList<>();



        //TODO: switch to use friendList
        //TODO: write an init()/refresh() method to reload Page
        //get list of current friends' emails

        emailList.addAll(friendList);
        Log.d(TAG,"Retrieved email list from Shared Preferences");

        //Pass in Friend List
        listAdapter = new FriendListExpandableListAdapter(this, emailList, true );


        friendExpandableList = (ExpandableListView) findViewById(R.id.expandable_friend_list_view);
        friendExpandableList.setAdapter(listAdapter);
        friendExpandableList.expandGroup(FRIEND_INDEX);
        friendExpandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            //Open Friend Home Page while passing in email address
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if( groupPosition != 2 ) {
                    return false;
                }
                //Grab the ExpandableListAdapter
                FriendListExpandableListAdapter myAdapter = ((FriendListExpandableListAdapter)parent.getExpandableListAdapter());
                activityMediator.preloadFriendWalkDays(myAdapter.getChild(groupPosition, childPosition).toString());
                Intent intent = new Intent( myAdapter.getActivity(), FriendSummary.class );
                intent.putExtra("email", myAdapter.getChild(groupPosition, childPosition).toString());
                myAdapter.getActivity().startActivity(intent);
                return true;
            }
        });
    }

    public boolean setOnChildClickListener(ExpandableListView parent, int groupPosition, int childPosition) {
        if( groupPosition != 2 ) {
            return false;
        }
        //Grab the ExpandableListAdapter
        launchFriendHomePage(parent, groupPosition, childPosition);
        return true;
    }

    public void launchFriendHomePage(ExpandableListView parent, int groupPosition, int childPosition) {
        FriendListExpandableListAdapter myAdapter = ((FriendListExpandableListAdapter)parent.getExpandableListAdapter());
        Intent intent = new Intent( myAdapter.getActivity(), FriendSummary.class );
        intent.putExtra(getString(R.string.intent_email_key), myAdapter.getChild(groupPosition, childPosition).toString());
        myAdapter.getActivity().startActivity(intent);
    }

    // create a custom action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friend_list_custom_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Called when button on action bar is pressed
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //which button in the action bar was selected?
        switch (item.getItemId()) {
            case R.id.add_friend_button:
                // User chose add friend button, open the dialogue.
                addFriendDialgue();
                return true;
            case android.R.id.home:
                finish();
                //User chose the exit button, return to home page
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Log.d(TAG, "" + item.getItemId());
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Make the Dialogue that adds a friend
     */
    public void addFriendDialgue() {
        //open new dialogue so we can add friend
        AlertDialog.Builder addFriendBuilder = new AlertDialog.Builder(this);
        addFriendBuilder.setTitle(R.string.add_friend_dialogue_title);
        addFriendBuilder.setMessage(R.string.add_friend_dialogue_description);

        //EditText for entering emails
        final EditText userEmail = new EditText(this);
        userEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        addFriendBuilder.setView(userEmail);

        //set buttons
        addFriendBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = userEmail.getText().toString();

                //Need to check if this email exists
                //FIXME don't think so actually, doesn't matter


                //TODO: Switch to call ActivityMediator.addFriend( userEmail, friendEmail)


                //currently won't handle refresh page, need to go somewhere else and go back
                activityMediator.addFriendByI(myEmail,email); // last arg is actually input email which is friend
                //saveNewFriend(email);
            }
        });
        addFriendBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        //Display Dialogue
        AlertDialog addFriendDialogue = addFriendBuilder.create();
        addFriendDialogue.setCanceledOnTouchOutside(false);
        addFriendDialogue.show();
    }

}
