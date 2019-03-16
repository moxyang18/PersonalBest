package com.example.team10.personalbest.friend;

import android.view.View;
import android.view.ViewGroup;

import com.example.team10.personalbest.FriendListPage;

public interface EListAdapter {
    public int getGroupCount();
    public int getChildrenCount(int groupPosition);
    public Object getGroup(int groupPosition);
    public Object getChild(int groupPosition, int childPosition);
    public long getGroupId(int groupPosition);
    public long getChildId(int groupPosition, int childPosition);
    public boolean hasStableIds();
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent);
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent);
    public boolean isChildSelectable(int groupPosition, int childPosition);
    public void addFriend(String email);
    public FriendListPage getActivity();
}
