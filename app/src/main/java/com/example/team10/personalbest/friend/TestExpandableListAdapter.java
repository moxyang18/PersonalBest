package com.example.team10.personalbest.friend;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import com.example.team10.personalbest.FriendListPage;

import java.util.ArrayList;

public class TestExpandableListAdapter implements EListAdapter {
    ExpandableListView expListView;
    FriendListExpandableListAdapter friendAdapter;
    ArrayList<String> emailList;
    FriendListPage activity;

    public TestExpandableListAdapter(Context listActivity, ArrayList<String> friendList, ExpandableListView listView ) {
        expListView = listView;
        emailList = friendList;
        activity = (FriendListPage)listActivity;
        friendAdapter = new FriendListExpandableListAdapter(listActivity, friendList);
        FriendListPage activity = (FriendListPage)listActivity;
        listView.setAdapter(friendAdapter);
        System.out.println("Inside TestAdapter constructor");
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                System.out.println("Setting up onChildClickListener");
                return activity.setOnChildClickListener(parent, groupPosition, childPosition);
            }
        });
    }

    public void click(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent ) {
        View friend = friendAdapter.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
        friend.performClick();
    }

    public void clickChild( View child, int childPosition ) {
        expListView.performItemClick( child, childPosition, child.getId() );
    }

    public void addFriend(String email) {
        emailList.add(email);
        friendAdapter.addFriend(email);
    }

    @Override
    public FriendListPage getActivity() {
        return activity;
    }

    public FriendListExpandableListAdapter getFriendAdapter() {
        return friendAdapter;
    }

    @Override
    public int getGroupCount() {
        return friendAdapter.getGroupCount();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return friendAdapter.getChildrenCount(groupPosition);
    }

    @Override
    public Object getGroup(int groupPosition) {
        return friendAdapter.getGroup(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return friendAdapter.getChild(groupPosition, childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return friendAdapter.getGroupId(groupPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return friendAdapter.getChildId(groupPosition, childPosition);
    }

    @Override
    public boolean hasStableIds() {
        return friendAdapter.hasStableIds();
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        return friendAdapter.getGroupView(groupPosition, isExpanded, convertView, parent);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return friendAdapter.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return friendAdapter.isChildSelectable(groupPosition, childPosition);
    }
}
