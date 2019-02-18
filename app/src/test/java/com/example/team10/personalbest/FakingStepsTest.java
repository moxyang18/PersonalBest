package com.example.team10.personalbest;

import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.team10.personalbest.fitness.GoogleFitAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class FakingStepsTest {
    private HomePage homepage;
    private Button goalButton;
    private TextView dailyStep;

    @Before
    public void init() {
        homepage = Robolectric.setupActivity(HomePage.class);
        goalButton = (Button)homepage.findViewById(R.id.currentGoal);
        dailyStep = (TextView)homepage.findViewById(R.id.stepsCount);

        homepage.setGoal(5000);

        homepage.fit = new GoogleFitAdapter(homepage);
        GoogleFitAdapter.setInstance(homepage.fit);
        homepage.fit.addObserver(homepage);
    }

    @After
    public void cleanup() {
        homepage.fit.deleteObserver(homepage);
    }
    /**
     * Tests that the update() in HomePage properly updates stepCount
     */
    @Test
    public void testStepUpdatesIncrSteps() {
        int stepCount = 0;

        int steps = 0;
        float distance = 0;
        long timeElapsed = 0;
        String timeString = "";

        Object results[]= {true, steps, distance, timeElapsed, timeString };


        //Have step increase by one each
        while( stepCount < 100 ) {
            homepage.fit.updateCustomResult(results);

            //Check HomePage Reflects Updated Steps properly
            assertEquals( Integer.toString(steps),homepage.step_text.getText());

            //Prepare for next loop
            steps += 1;
            results[1] = steps;
            stepCount += 1;
        }

    }

    @Test
    public void testStepUpdatesDecrSteps() {
        int stepCount = 100;

        int steps = 0;
        float distance = 0;
        long timeElapsed = 0;
        String timeString = "";

        Object results[]= {true, steps, distance, timeElapsed, timeString };


        //Have step increase by one each
        while( stepCount > 0 ) {
            homepage.fit.updateCustomResult(results);

            //Check HomePage Reflects Updated Steps properly
            assertEquals( Integer.toString(steps),homepage.step_text.getText());

            //Prepare for next loop
            steps += 1;
            results[1] = steps;
            stepCount -= 1;
        }
    }
}
