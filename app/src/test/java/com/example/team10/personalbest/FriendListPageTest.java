package com.example.team10.personalbest;

import static org.junit.Assert.*;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.time.LocalDate;

@RunWith(RobolectricTestRunner.class)
public class FriendListPageTest {

    HomePage homePage;
    FriendListPage friendListPage;
    ExpandableListView friendList;

    String startDay;

    @Before
    public void init() {
        friendListPage = Robolectric.setupActivity(FriendListPage.class);
        friendList = friendListPage.findViewById(R.id.expandable_friend_list_view);
        //friendList.get

        // Determine the day of the week so we can start on Sunday
        String dayOfWeek = LocalDate.now().getDayOfWeek().toString();

        // How many days must we subtract to get to sunday?
        int minDays = 0;
        switch (dayOfWeek) {
            case "SUNDAY":
                minDays = 0;
                break;
            case "MONDAY":
                minDays = 1;
                break;
            case "TUESDAY":
                minDays = 2;
                break;
            case "WEDNESDAY":
                minDays = 3;
                break;
            case "THURSDAY":
                minDays = 4;
                break;
            case "FRIDAY":
                minDays = 5;
                break;
            case "SATURDAY":
                minDays = 6;
                break;
        }

    }
}
