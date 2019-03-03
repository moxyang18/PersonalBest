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

import java.util.Observable;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class FakingStepsTest {
    private HomePage homepage;
    private Button goalButton;
    private TextView dailyStep;
    private ActivityMediator mediator;
    private FakeFit f;
    @Before
    public void init() {
        homepage = Robolectric.setupActivity(HomePage.class);
        goalButton = (Button)homepage.findViewById(R.id.currentGoal);
        dailyStep = (TextView)homepage.findViewById(R.id.stepsCount);
        mediator = (ActivityMediator)homepage.getTestMediator();

        f = new FakeFit();
        f.addObserver(mediator);
        mediator.resetDay();

    }

    @After
    public void cleanup() {
        mediator.resetDay();
    }

    /**
     * Tests that the update() in HomePage properly updates stepCount
     */

    @Test
    public void testStepUpdatesIncrSteps() {
       int stepCount =0;
       int steps =0;
        //Have step increase by one each
        while( stepCount < 100 ) {

            f.setStep(steps);
            //Check HomePage Reflects Updated Steps properly
            assertEquals( Integer.toString(steps),homepage.step_text.getText());

            //Prepare for next loop
            steps += 1;
            stepCount += 1;
        }

    }

    @Test
    public void testStepUpdatesDecrSteps() {
        int stepCount = 100;

        int steps = 100;

        //Have step increase by one each
        while( stepCount > 0 ) {
            f.setStep(steps);
            //Check HomePage Reflects Updated Steps properly
            assertEquals( Integer.toString(steps),homepage.step_text.getText());

            //Prepare for next loop
            steps -= 1;

            stepCount -= 1;
        }
    }


    private class FakeFit extends Observable{
        int steps = 0;
        float distance = 0;
        long timeElapsed = 0;
        String timeString = "";

        private Object result[]= {false, steps, distance, timeElapsed, timeString };



        public void setStep(int s){
            result[1] = s;
            setChanged();
            notifyObservers(f.result);
        }
    }
}
