package com.example.team10.personalbest;

import android.content.Intent;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;

import static org.junit.Assert.*;

import org.junit.After;
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
public class RunningModeTest {
    RunningMode runningMode;
    Button add_step_button;
    Button end_run_button;
    TextView currentGoal;
    TextView intentionalSteps;
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
        HomePage homePage = Robolectric.buildActivity(HomePage.class,intent).create().get();

        //mockMediator = new MockMediator();
        runningMode = Robolectric.setupActivity(RunningMode.class);
        //mockMediator.mockStartActivity();
        FakeFit fit = FakeFit.getInstance();
        //mockMediator.linkRunning(runningMode);

        end_run_button = runningMode.findViewById(R.id.end_run);
        add_step_button = runningMode.findViewById(R.id.add_steps_in_running);
        currentGoal = runningMode.findViewById(R.id.goal_running_mode);
        totalSteps = runningMode.findViewById(R.id.total_steps_rm);
        intentionalSteps = runningMode.findViewById(R.id.running_steps);

    }

    @Test
    public void mockStepsTest1() {

        // when no step is taken
        System.out.println(totalSteps.getText().toString());
        assertEquals(totalSteps.getText().toString(), "0");

    }

    @Test
    public void mockStepsTest2() {

        // mock 500 steps
        add_step_button.performClick();
        assertEquals(intentionalSteps.getText().toString(), "500");
        assertEquals(totalSteps.getText().toString(), "500");
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
        assertEquals("3000",intentionalSteps.getText().toString());
        assertEquals("3500", totalSteps.getText().toString() );

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
        assertEquals("5000",intentionalSteps.getText().toString());
        assertEquals("8500",totalSteps.getText().toString());

    }

    @Test
    public void mockTimeTest() {

        assertTrue(true);

    }

    @After
    public void cleanUp(){
        runningMode.finish();
        MockMediator.instance = null;
    }


}