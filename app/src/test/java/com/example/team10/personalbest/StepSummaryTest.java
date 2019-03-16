package com.example.team10.personalbest;

import static org.junit.Assert.*;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;

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
public class StepSummaryTest {

    HomePage homePage;
    StepSummary userSummary;
    HashMap<String, WalkDay> user_walkdays;
    String userEmail;
    String userDisplayName;
    BarChart barChart;

    @Before
    public void init() {
        //ActivityMediator activityMediator = ActivityMediator.getInstance();
        user_walkdays = new HashMap<>();
    }

    public void setup0(){
        LocalDate currentDay = LocalDate.now();
        // populate the hashmap with the walkday data
        for(int i =0; i< 36; i++){
            WalkDay currentWalkDay = new WalkDay(currentDay.toString());
            currentWalkDay.setStepCountIntentional(0);
            currentWalkDay.setStepCountUnintentional(0);
            currentWalkDay.setStepCountDailyTotal(0);
            currentWalkDay.setGoal(6000);
            user_walkdays.put(currentDay.toString(), currentWalkDay);
            currentDay = currentDay.minusDays(1);
        }

        ActivityMediator.setUserWalkDays(user_walkdays);
        userSummary = Robolectric.setupActivity(StepSummary.class);
        barChart = userSummary.getBarChart();

    }

    public void setup1(){
        LocalDate currentDay = LocalDate.now();
        // populate the hashmap with the walkday data
        for(int i =0; i< 28; i++){
            WalkDay currentWalkDay = new WalkDay(currentDay.toString());
            currentWalkDay.setStepCountIntentional(2000);
            currentWalkDay.setStepCountUnintentional(3000);
            currentWalkDay.setStepCountDailyTotal(5000);
            currentWalkDay.setGoal(6000);
            user_walkdays.put(currentDay.toString(), currentWalkDay);
            currentDay = currentDay.minusDays(1);
        }

        ActivityMediator.setUserWalkDays(user_walkdays);
        userSummary = Robolectric.setupActivity(StepSummary.class);
        barChart = userSummary.getBarChart();
    }

    public void setup2(){
        LocalDate currentDay = LocalDate.now();
        // populate the hashmap with the walkday data
        for(int i =0; i< 4; i++){
            WalkDay currentWalkDay = new WalkDay(currentDay.toString());
            currentWalkDay.setStepCountIntentional(2000);
            currentWalkDay.setStepCountUnintentional(3000);
            currentWalkDay.setStepCountDailyTotal(5000);
            currentWalkDay.setGoal(6000);
            user_walkdays.put(currentDay.toString(), currentWalkDay);
            currentDay = currentDay.minusDays(1);
        }

        for(int i =14; i< 28; i++){
            WalkDay currentWalkDay = new WalkDay(currentDay.toString());
            currentWalkDay.setStepCountIntentional(5000);
            currentWalkDay.setStepCountUnintentional(3000);
            currentWalkDay.setStepCountDailyTotal(8000);
            currentWalkDay.setGoal(6000);
            user_walkdays.put(currentDay.toString(), currentWalkDay);
            currentDay = currentDay.minusDays(1);
        }

        ActivityMediator.setUserWalkDays(user_walkdays);
        userSummary = Robolectric.setupActivity(StepSummary.class);
        barChart = userSummary.getBarChart();
    }

    private void setup3() {
        LocalDate currentDay = LocalDate.now();
        // populate the hashmap with the walkday data
        for(int i =0; i< 4; i++){
            WalkDay currentWalkDay = new WalkDay(currentDay.toString());
            currentWalkDay.setStepCountIntentional(2000);
            currentWalkDay.setStepCountUnintentional(3000);
            currentWalkDay.setStepCountDailyTotal(5000);
            currentWalkDay.setGoal(6000);
            user_walkdays.put(currentDay.toString(), currentWalkDay);
            currentDay = currentDay.minusDays(1);
        }

        for(int i =14; i< 28; i++){
            WalkDay currentWalkDay = new WalkDay(currentDay.toString());
            currentWalkDay.setStepCountIntentional(5000);
            currentWalkDay.setStepCountUnintentional(3000);
            currentWalkDay.setStepCountDailyTotal(8000);
            currentWalkDay.setGoal(6000);
            user_walkdays.put(currentDay.toString(), currentWalkDay);
            currentDay = currentDay.minusDays(1);
        }

        for(int i =28; i< 36; i++){
            WalkDay currentWalkDay = new WalkDay(currentDay.toString());
            currentWalkDay.setStepCountIntentional(5000);
            currentWalkDay.setStepCountUnintentional(3000);
            currentWalkDay.setStepCountDailyTotal(8000);
            currentWalkDay.setGoal(6000);
            user_walkdays.put(currentDay.toString(), currentWalkDay);
            currentDay = currentDay.minusDays(1);
        }

        ActivityMediator.setUserWalkDays(user_walkdays);
        userSummary = Robolectric.setupActivity(StepSummary.class);
        barChart = userSummary.getBarChart();
    }

    /* Test if the barchart shows nothing when no steps were taken in that week */
    @Test
    public void testNoActivity() {

        setup0();

        assertTrue( barChart.getBarData().getEntryCount() == 28 );

        assertTrue( barChart.getBarData().getDataSetByIndex(0).getStackSize() == 2);

        // test whether there is no data displayed on the chart
        System.out.println(barChart.getBarData().getDataSetByIndex(0).getYMax());

        System.out.println(barChart.getBarData().getDataSetByIndex(0).toString());
        assertTrue(barChart.getBarData().getDataSetByIndex(0).getYMax() == 0);

        assertTrue(barChart.getBarData().getDataSetByIndex(0).getYMin() == 0);
        System.out.println(barChart.getBarData().getDataSetByIndex(0).toString());
    }


    /* Test if the barchart shows everyday's bars with correct values */
    @Test
    public void testEveryDayBars (){
        setup1();
        System.out.println("Testing each entry:");
        System.out.println(barChart.getBarData().getDataSetByIndex(0).toString());
        float [] y = barChart.getBarData().getDataSets().get(0).getEntryForIndex(0).getYVals();
        assertTrue(y.length == 2);
        assertTrue(barChart.getBarData().getDataSetByIndex(0).getYMax() == 5000.0);

        // System.out.println(barChart.getBarData().getDataSetByIndex(0).getYMin());
        // assertTrue(barChart.getBarData().getDataSetByIndex(0).getYMin() == 5000.0);

        assertTrue( barChart.getBarData().getEntryCount() == 28 );
    }


    /* Test whether the barchart shows only the days that have steps been taken with different
     * values */
    @Test
    public void testDifferentDayBars (){
        setup2();
        assertTrue( barChart.getBarData().getEntryCount() == 28 );
        System.out.println("Testing each entry:");
        System.out.println(barChart.getBarData().getDataSetByIndex(0).toString());
        float [] y = barChart.getBarData().getDataSets().get(0).getEntryForIndex(0).getYVals();
        assertTrue(y.length == 2);
        assertTrue(barChart.getBarData().getDataSetByIndex(0).getYMax() == 8000.0);
        assertTrue(barChart.getBarData().getDataSetByIndex(0).getYMin() == 0.0);
    }


    /* Test whether the limit lines correctly corresponds to the daily goal */
    @Test
    public void testGoalLines (){
        setup2();
        List<LimitLine> a = barChart.getAxisLeft().getLimitLines();
        assertTrue(a.size() <= 28);
        System.out.println(a.toString());
    }


    /* Test whether the graph correctly shows up to 28 entries with most recently vals */
    @Test
    public void test4 (){
        setup3();
        assertTrue( barChart.getBarData().getEntryCount() == 28 );
        System.out.println("Testing each entry:");
        System.out.println(barChart.getBarData().getDataSetByIndex(0).toString());
        float [] y = barChart.getBarData().getDataSets().get(0).getEntryForIndex(0).getYVals();
        assertTrue(y.length == 2);
        assertTrue(barChart.getBarData().getDataSetByIndex(0).getYMax() == 8000.0);
        assertTrue(barChart.getBarData().getDataSetByIndex(0).getYMin() == 0.0);
    }


    /* Test whether the graph correctly shows up to 28 entries with most recently vals */
    @Test
    public void testIndexCorrespondence (){
        setup3();
        assertTrue( barChart.getBarData().getEntryCount() == 28 );
        System.out.println("Testing each entry:");
        System.out.println(barChart.getBarData().getDataSetByIndex(0).toString());
        float [] y = barChart.getBarData().getDataSets().get(0).getEntryForIndex(0).getYVals();
        assertTrue(y.length == 2);
        assertTrue(barChart.getBarData().getDataSetByIndex(0).getYMin() == 0.0);
    }

}
