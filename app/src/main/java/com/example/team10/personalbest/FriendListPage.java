package com.example.team10.personalbest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.example.team10.personalbest.friend.FriendListExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FriendListPage extends AppCompatActivity {
    private String TAG = "FriendListPage:";

    ExpandableListView friendExpandableList;
    FriendListExpandableListAdapter listAdapter;

    ArrayList<String> friendNames; //TODO delete later?
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO update to pull from shared preference later
        SharedPreferences friendPreferences = getSharedPreferences("friend_list", MODE_PRIVATE);
        SharedPreferences.Editor editor = friendPreferences.edit();
        ArrayList<String> emailList = new ArrayList<>(); //TODO Grab from shared Preference


        //get list of current friends' emails
        Set<String> emailSet = friendPreferences.getStringSet("emailList", new HashSet<String>());
        emailList.addAll(emailSet);
        Log.d(TAG,"Retrieved email list from Shared Preferences");

        //Pass in Friend List
        listAdapter = new FriendListExpandableListAdapter(this, emailList);


        friendExpandableList = (ExpandableListView) findViewById(R.id.expandable_friend_list_view);
        friendExpandableList.setAdapter(listAdapter);
        friendExpandableList.expandGroup(2); //TODO magic number get rid
        friendExpandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            //Open Friend Home Page while passing in email address
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if( groupPosition != 2 ) {
                    return false;
                }
                //Grab the ExpandableListAdapter
                FriendListExpandableListAdapter myAdapter = ((FriendListExpandableListAdapter)parent.getExpandableListAdapter());
                Intent intent = new Intent( myAdapter.getActivity(), FriendHomePage.class );
                intent.putExtra("email", myAdapter.getChild(groupPosition, childPosition).toString());
                myAdapter.getActivity().startActivity(intent);
                return true;
            }
        });
    }

    public boolean saveNewFriend(String email) {
        //method which finds the new friend in our system TODO


        //Found the friend in our system, now save in Shared Preferences/update friend count
        SharedPreferences friendPreferences = getSharedPreferences("friend_list", MODE_PRIVATE);
        SharedPreferences.Editor editor = friendPreferences.edit();

        //get list of current friends' emails
        Set<String> emailSet = friendPreferences.getStringSet("emailList", new HashSet<String>());
        Log.d(TAG,"Retrieved email set from Shared Preferences");

        //add new email to set and save into Shared Preferences
        emailSet.add(email);
        editor.putStringSet("emailList", emailSet);
        editor.apply();
        Log.d(TAG, "Saved New List Successfully");

        //let ExpandableList know new stuff
        listAdapter.addFriend(email);
        return true;
    }

}
