package com.example.team10.personalbest;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

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
    MockMediator mockMediator;

    @Before
    public void init() {

        MediatorFactory.put("MOCK_MEDIATOR", new MediatorFactory.BluePrint() {
            @Override
            public Mediator create(HomePage homePage) {
                return new MockMediator(homePage);
            }
        });
        Intent intent = new Intent(RuntimeEnvironment.application,HomePage.class);
        intent.putExtra("GET_MEDIATOR","MOCK_MEDIATOR");
        homePage = Robolectric.buildActivity(HomePage.class,intent).create().get();
        mockMediator = MockMediator.getInstance();
        //mockMediator = new MockMediator();
        //mockMediator.setHomePage(homePage);
        //mockMediator.mockStartActivity();
        FakeFit fit = FakeFit.getInstance();

        own_summary_button = homePage.findViewById(R.id.selfMonthlyChart);
        add_step_button = homePage.findViewById(R.id.addStepButton);
        currentGoal = homePage.findViewById(R.id.currentGoal);
        totalSteps = homePage.findViewById(R.id.stepsCount);
    }

    @Test
    public void mockStepsTest1() {

        // when no step is taken
        System.out.println(totalSteps.getText().toString());
        assertEquals( "0",totalSteps.getText().toString());

    }

    @Test
    public void mockStepsTest2() {

        // mock 500 steps
        add_step_button.performClick();
        assertTrue(totalSteps.getText().toString().equals("500"));

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
        assertTrue(totalSteps.getText().toString().equals("3500"));

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
        assertTrue(totalSteps.getText().toString().equals("8500"));

    }

    @Test
    public void mockTimeTest() {

        assertTrue(true);

    }


}