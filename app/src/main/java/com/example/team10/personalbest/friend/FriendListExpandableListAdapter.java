package com.example.team10.personalbest.friend;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.Image;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.team10.personalbest.FriendHomePage;
import com.example.team10.personalbest.FriendListPage;
import com.example.team10.personalbest.HomePage;
import com.example.team10.personalbest.R;

import java.util.ArrayList;

/**
 * ExpandableListAdapter explained here: https://www.journaldev.com/9942/android-expandablelistview-example-tutorial
 */

/**
 * Android ExpandableListView is a view that shows items in a vertically scrolling two-level list.
 * It differs from a ListView by allowing two levels which are groups that can be easily expanded
 * and collapsed by touching to view and their respective children items.
 */

public class FriendListExpandableListAdapter extends BaseExpandableListAdapter {
    private String TAG = "FriendListExpandableListAdapter:";
    FriendListPage activity;
    ArrayList<String> lists;
    ArrayList<String> incomingRequest;
    ArrayList<String> outgoingRequest;
    ArrayList<String> friends;

    //Currently incomingRrequesting and outGoingRequest is postponed.
    //Note:
    //Add Friend Mechanic: Only need to add email address to see their information.

    //TODO revise constructor to take in incoming/outgoing request string arraylist
    public FriendListExpandableListAdapter(Context listActivity, ArrayList<String> friendList ) {
        //init everything
        friends = friendList;
        incomingRequest = new ArrayList<>();
        outgoingRequest = new ArrayList<>();
        lists = new ArrayList<>();
        activity = (FriendListPage)listActivity;


        //make the list title
        lists.add( activity.getString(R.string.incoming_request_header) );
        lists.add( activity.getString(R.string.outgoing_request_header) );
        lists.add( activity.getString(R.string.friend_list_header) );

        //set onclickLIstner for child


    }
    @Override
    public int getGroupCount() {
        return 3; //TODO get rid of magic number
    }

    /**
     * done
     * @param groupPosition
     * @return
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        if( groupPosition == 0 ) {
            return incomingRequest.size();
        }
        else if( groupPosition == 1 ) {
            return outgoingRequest.size();
        }
        else {
            return friends.size();
        }
    }

    /**
     * Return sub lists
     * @param groupPosition
     * @return
     */
    @Override
    public Object getGroup(int groupPosition) {
        if( groupPosition == 0 ) {
            return incomingRequest;
        }
        else if( groupPosition == 1 ) {
            return outgoingRequest;
        }
        else {
            return friends;
        }
    }

    /**
     * Get the child in sublist[groupPosition] in the [childPosition]'th index
     * @param groupPosition
     * @param childPosition
     * @return
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        //TODO switch to constants and switch cases?
        if( groupPosition == 0 ) {//TODO check if 0 based?
            return incomingRequest.get(childPosition);
        }
        if( groupPosition == 1 ) {
            return outgoingRequest.get(childPosition);
        }
        else {
            return friends.get(childPosition);
        }
    }

    /**
     *
     * @param groupPosition
     * @return
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Returns the view that will display the group/sublist
     * @param groupPosition
     * @param isExpanded
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String listTitle = lists.get(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.activity.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.friend_group_view, null);
        }
        TextView listNameText = (TextView) convertView
                .findViewById(R.id.group_name_text);
        listNameText.setTypeface(null, Typeface.BOLD);
        listNameText.setText(listTitle);

        //important to allow group to open
        ImageButton addButton = (ImageButton)convertView.findViewById(R.id.plus_img_button);
        //TODO onclick listener to add Friend. here .
        addButton.setFocusable(false);
        addButton.setOnClickListener( new View.OnClickListener() {

            /**
             * Open up a new dialogue? maybe use iterator. check later.
             * TODO finish up
             * @param v
             */
            @Override
            public void onClick(View v) {
                //open new dialogue so we can add friend
                AlertDialog.Builder addFriendBuilder = new AlertDialog.Builder(activity);
                addFriendBuilder.setTitle(R.string.add_friend_dialogue_title);
                addFriendBuilder.setMessage(R.string.add_friend_dialogue_description);

                //EditText for entering emails
                final EditText userEmail = new EditText(activity);
                userEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                addFriendBuilder.setView(userEmail);

                //set buttons
                addFriendBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = userEmail.getText().toString();
                        activity.saveNewFriend(email);
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
        });

        return convertView;
    }

    /**
     * Creates and returns the child view (friend in friend list)
     * @param groupPosition
     * @param childPosition
     * @param isLastChild
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String email = (String) getChild(groupPosition, childPosition);

        //convertView is TODO
        if (convertView == null) {
            //set layout for childview
            LayoutInflater layoutInflater = (LayoutInflater) this.activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.friend_item_view, null);
        }

        TextView friendItemView = (TextView) convertView
                .findViewById(R.id.friend_name_text);

        friendItemView.setText(email);

        //Set open chat box
        ImageButton chatButton = (ImageButton)convertView.findViewById(R.id.chat_img_button);
        //TODO onclick listener to open messaging activity, finish up.
        chatButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                Intent intent = new Intent(activity, MessagePage.class );
                intent.putExtra("friend", email ); //pass in name
                activity.startActivity(intent);
                 **/
            }
        });
        //important to allow group to open
        chatButton.setFocusable(false);

        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        if( groupPosition == 2 ) {
            return true;
        }
        return false;
    }

    public void addFriend(String email) {
        friends.add(email);
        notifyDataSetChanged();
        Log.d(TAG, email);
    }

    public FriendListPage getActivity() {
        return this.activity;
    }


}
