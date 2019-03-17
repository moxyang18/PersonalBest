package com.example.team10.personalbest;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import static org.junit.Assert.*;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowActivity;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class StepSummaryExtraScenarioTest {
    RunningMode runningMode;
    Button add_step_button;
    Button end_run_button;
    TextView currentGoal;
    TextView intentionalSteps;
    TextView totalSteps;
    MockMediator mockMediator;
    HomePage homePage;
    StepSummary userSummary;
    HashMap<String, WalkDay> user_walkdays;
    String userEmail;
    Button own_summary_button;
    ShadowActivity shadowActivity;
    FakeFit fit ;
    BarChart barChart;


    /*
    Plan:
    Anon for the first time starts using Personal Best.
    First Day: Anon opens Personal Best, takes 440 steps over his unintentional walks, and press “Start Walk/Run” to take 2000 steps.
    He then presses “Back” to go to the homepage.

    Anon wants to see his monthly step summary, even though he just started on his first day. He presses “Show Monthly Summary” button.
    Anon sees a bar chart in which the x-axis represents the last 28 days, and the y-axis represents the steps. He finds only on that day there is a vertical stacked bar that represents the steps he just did, 500 steps on the lower stack as the unintentional steps and 2000 steps on the upper stack as the intentional steps. (Milestone 2, User Story 3)

    For the next 2 days, Anon walked for 5000 steps on each day.
    Then he checked his monthly summary by pressing “Show Monthly Summary” and saw his walking steps from 02/03 to 03/02 and there are 3 consecutive bars from the rightmost of the chart that correspond to these 3 days’ walk he took.
    */
    @Before
    public void setUp() throws Exception {
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
        MockMediator.userEmail = "anon@gmail.com";
        MockMediator.userDisplayName = "Anon";
        fit = FakeFit.getInstance();

        mockMediator.timeTravelBackward();
        mockMediator.timeTravelBackward();
        //starts 2 day behind;

    }

    @Test
    public void testTakingIntentionalWalkAndGoBack(){
        runningMode = Robolectric.setupActivity(RunningMode.class);
        Object [] arr = {true,440,0.f,true};
        fit.setResult(arr);
        assertTrue(((TextView)(runningMode.findViewById(R.id.running_steps))).getText().toString().equals("440"));
        assertTrue(((TextView)(runningMode.findViewById(R.id.total_steps_rm))).getText().toString().equals("440"));
        runningMode.finish();
    }

    @Test
    public void testTakingUnntentionalWalknNextDay(){
        mockMediator.timeTravelForward();
        Object [] arr = {true,5000,0.f,false};
        fit.setResult(arr);
        assertTrue(((TextView)(homePage.findViewById(R.id.stepsCount))).getText().toString().equals("5000"));
    }

    @Test
    public void testTakingUnntentionalWalknToday(){
        mockMediator.timeTravelNow();
        Object [] arr = {true,5000,0.f,false};
        fit.setResult(arr);
        assertTrue(((TextView)(homePage.findViewById(R.id.stepsCount))).getText().toString().equals("5000"));
    }

    @Test
    public void testStepSummary(){
        userSummary = Robolectric.setupActivity(StepSummary.class);
        barChart = userSummary.getBarChart();
        assertTrue( barChart.getBarData().getEntryCount() == 28 );

        assertTrue( barChart.getBarData().getDataSetByIndex(0).getStackSize() == 2);

        assertTrue(barChart.getBarData().getDataSetByIndex(0).getYMax() == 5000.0);
        assertTrue(barChart.getBarData().getDataSetByIndex(0).getYMin() == 0.0);
        float [] y = barChart.getBarData().getDataSets().get(0).getEntryForIndex(25).getYVals();
        System.out.println("The day before yesterday step is: "+y[0]);
        assertTrue(y.length == 2);
        assertTrue(y[1] == 440.f);
        y = barChart.getBarData().getDataSets().get(0).getEntryForIndex(26).getYVals();
        System.out.println("Yesterday step is: "+y[0]);
        assertTrue(y.length == 2);
        assertTrue(y[0] == 5000.f);
        y = barChart.getBarData().getDataSets().get(0).getEntryForIndex(27).getYVals();
        assertTrue(y.length == 2);
        System.out.println("Today step is: "+y[0]);
        assertTrue(y[0] == 5000.f);

    }

    @After
    public void cleanUp(){
        homePage.finish();
        MockMediator.instance = null;
        MockMediator.reset();
    }



}