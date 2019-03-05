package com.example.team10.personalbest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;

import com.example.team10.personalbest.friend.FriendListExpandableListAdapter;

import java.util.ArrayList;

public class FriendListPage extends AppCompatActivity {
    ExpandableListView friendExpandableList;
    FriendListExpandableListAdapter listAdapter;

    ArrayList<String> friendNames;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Grab Friends List from shared preferences, saved as Arr
        friendNames = new ArrayList<>();
        friendNames.add("Kelly");
        friendNames.add("Henry");
        friendNames.add("Joshua");
        friendNames.add("Moxuan");
        friendNames.add("Daniel");
        friendNames.add("Zhen");

        //Pass in Friend List
        listAdapter = new FriendListExpandableListAdapter(this, friendNames);

        friendExpandableList = (ExpandableListView) findViewById(R.id.expandable_friend_list_view);
        friendExpandableList.setAdapter(listAdapter);
        friendExpandableList.expandGroup(2); //TODO magic number get rid

    }

    //Settle the Buttons
    public void onClickChatButton(String name) {

    }

}
