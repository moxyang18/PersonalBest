package com.example.team10.personalbest;

import android.widget.Button;
import android.widget.TextView;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class HomePageTest {
    HomePage homePage;
    Button own_summary_button;
    Button add_step_button;
    TextView currentGoal;
    TextView totalSteps;

    @Before
    public void init() {
        homePage = Robolectric.setupActivity(HomePage.class);

        own_summary_button = homePage.findViewById(R.id.selfMonthlyChart);
        add_step_button = homePage.findViewById(R.id.addStepButton);
        currentGoal = homePage.findViewById(R.id.currentGoal);
        totalSteps = homePage.findViewById(R.id.stepsCount);
    }

    @Test
    public void mockStepsTest1() {

        // when no step is taken
        System.out.println(totalSteps.getText().toString());
        assertEquals(totalSteps.getText().toString(), "TextView");

    }
/*
    @Test
    public void mockStepsTest2() {

        // mock 500 steps
        add_step_button.performClick();
        assertTrue(totalSteps.getText().toString() == "500");

    }

    @Test
    public void mockStepsTest3() {

        // mock 3000 steps
        add_step_button.performClick();
        add_step_button.performClick();
        add_step_button.performClick();
        add_step_button.performClick();
        add_step_button.performClick();
        add_step_button.performClick();
        assertTrue(totalSteps.getText().toString() == "3000");

    }

    @Test
    public void mockStepsTest4() {

        // mock 5000 steps
        add_step_button.performClick();
        add_step_button.performClick();
        add_step_button.performClick();
        add_step_button.performClick();
        add_step_button.performClick();
        add_step_button.performClick();
        add_step_button.performClick();
        add_step_button.performClick();
        add_step_button.performClick();
        add_step_button.performClick();
        assertTrue(totalSteps.getText().toString() == "5000");

    }
*/
    @Test
    public void mockTimeTest() {

        assertTrue(true);

    }


}