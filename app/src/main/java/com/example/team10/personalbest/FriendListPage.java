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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FriendListPage extends AppCompatActivity {
    private String TAG = "FriendListPage:";

    //final int INCOMING_INDEX = 0;
    //final int OUTGOING_INDEX = 0;
    final int FRIEND_INDEX = 2;

    ExpandableListView friendExpandableList;
    FriendListExpandableListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //newly added

        //Get FriendList from Shared Preference
        SharedPreferences friendPreferences = getSharedPreferences(getString(R.string.shared_pref_file_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = friendPreferences.edit();
        ArrayList<String> emailList = new ArrayList<>();


        //get list of current friends' emails
        Set<String> emailSet = friendPreferences.getStringSet(getString(R.string.shared_pref_string_set_key), new HashSet<String>());
        emailList.addAll(emailSet);
        Log.d(TAG,"Retrieved email list from Shared Preferences");

        //Pass in Friend List
        listAdapter = new FriendListExpandableListAdapter(this, emailList);


        friendExpandableList = (ExpandableListView) findViewById(R.id.expandable_friend_list_view);
        friendExpandableList.setAdapter(listAdapter);
        friendExpandableList.expandGroup(FRIEND_INDEX);
        friendExpandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            //Open Friend Home Page while passing in email address
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                return setOnChildClickListener(parent, groupPosition, childPosition);
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
        Intent intent = new Intent( myAdapter.getActivity(), FriendHomePage.class );
        intent.putExtra(getString(R.string.intent_email_key), myAdapter.getChild(groupPosition, childPosition).toString());
        myAdapter.getActivity().startActivity(intent);
        System.out.println( "inside launchFreind Home Page");
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
     * A user with the email exists, so we can save this person as our friend.
     * @param email
     * @return
     */
    public boolean saveNewFriend(String email) {
        //method which finds the new friend in our system TODO


        //Found the friend in our system, now save in Shared Preferences/update friend count
        SharedPreferences friendPreferences = getSharedPreferences(getString(R.string.shared_pref_file_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = friendPreferences.edit();

        //get list of current friends' emails
        Set<String> emailSet = friendPreferences.getStringSet(getString(R.string.shared_pref_string_set_key), new HashSet<String>());
        Log.d(TAG,"Retrieved email set from Shared Preferences");


        //Remove the email from sharedpreference DONT DELETE
        editor.remove(getString(R.string.shared_pref_string_set_key));
        editor.apply();


        //add new email to set and save into Shared Preferences
        emailSet.add(email);
        editor.putStringSet(getString(R.string.shared_pref_string_set_key), emailSet);
        editor.apply();
        Log.d(TAG, "Saved New List Successfully");

        //let ExpandableList know new stuff
        listAdapter.addFriend(email);
        return true;
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

                //TODO Need to check if this email exists
                saveNewFriend(email);
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
