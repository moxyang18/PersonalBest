package com.example.team10.personalbest;
import android.widget.Button;
import android.widget.TextView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class TestUpdateGoal {
    /**
     * Testing Update Goals:
     * -Tests checkGoals() method
     */

    HomePage homepage;
    Button goalButton;
    TextView dailyStep;

    @Before
    public void init() {
        homepage = Robolectric.setupActivity(HomePage.class);
        goalButton = (Button)homepage.findViewById(R.id.currentGoal);
        dailyStep = (TextView)homepage.findViewById(R.id.stepsCount);
    }

    /**
     *     Helper class which initializes goals text and daily steps so that StepCount < GoalText
     *     GoalText = 5000
     *     StepCount = 1000
     */
    private void setup() {
        homepage.setGoalMet(false);
        homepage.setStepCount(1000);
        homepage.showStepCount();
        homepage.setGoalText(5000);

    }

    //Tests that pressing on the Goal Button allows to customize the goal
    @Test
    public void testRecommendedGoal() {
        assertTrue( goalButton.performClick() ); //checks a listener was assigned to button

    }

    /**
     * 1. Testing checkGoal()
     */
    //test checkGoal (check below, equal, greater than) goal is met. 6 test cases
    @Test
    public void testCheckGoalGreaterThanGoalAndGoalNotMet() {
        //initializing
        setup();
        //GoalText currently at 5000, daily steps at 1000
        homepage.setGoalMet(false);
        homepage.setStepCount(5500);
        homepage.showStepCount();

        //Stepcount now > Goal
        assertTrue( homepage.checkGoal()) ;
        assertTrue( homepage.getGoalMet());
    }

    @Test
    public void testCheckGoalGreaterThanGoalAndGoalMet() {
        //initializing
        setup();

        //GoalText currently at 5000, daily steps at 1000
        homepage.setGoalMet(true);
        homepage.setStepCount(5500);
        homepage.showStepCount();

        //Stepcount now < Goal
        assertFalse( homepage.checkGoal()); //nothing should be changed
        assertTrue( homepage.getGoalMet()); //should not have been changed
    }

    @Test
    public void testCheckGoalLessThanGoalAndGoalNotMet() {
        //initializing
        setup();

        //GoalText currently at 5000, daily steps at 1000
        homepage.setGoalMet(false);
        homepage.setStepCount(4500); //already lower than the goal but whatevs
        homepage.showStepCount();

        //Stepcount now < Goal
        assertFalse( homepage.checkGoal()); //nothing should be changed
        assertFalse( homepage.getGoalMet()); //should not have been changed
    }

    @Test
    public void testCheckGoalLessThanGoalAndGoalMet() {
        //initializing
        setup();

        //GoalText currently at 5000, daily steps at 1000
        homepage.setGoalMet(true);
        homepage.setStepCount(4500); //already lower than the goal but whatevs
        homepage.showStepCount();

        //Stepcount now > Goal
        assertFalse( homepage.checkGoal()); //nothing should be changed
        assertTrue( homepage.getGoalMet()); //should not have been changed
    }

    @Test
    public void testCheckGoalEqualToGoalAndGoalNotMet() {
        //initializing
        setup();

        //GoalText currently at 5000, daily steps at 1000
        homepage.setGoalMet(false);
        homepage.setStepCount(5000);
        homepage.showStepCount();


        //Stepcount now = Goal
        assertTrue( homepage.checkGoal()); //should be changed
        assertTrue( homepage.getGoalMet()); //should not have been changed
    }

    @Test
    public void testCheckGoalEqualToGoalAndGoalMet() {
        //initializing
        setup();

        //GoalText currently at 5000, daily steps at 1000
        homepage.setGoalMet(true);
        homepage.setStepCount(5000);
        homepage.showStepCount();

        //Stepcount now = Goal
        assertFalse( homepage.checkGoal()); //nothing should be changed
        assertTrue( homepage.getGoalMet()); //should not have been changed
    }
}
