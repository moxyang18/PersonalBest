package com.example.team10.personalbest;

import android.app.Activity;
import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import java.time.LocalDate;

public class DataProcessorTest {

    DataProcessor dp;
    HomePage hp;

    @Test
    public void loadIntoHomePage() {
    }

    /**
     * Makes sure we get an instance back after setting
     */
    @Test
    public void setInstance() {
        DataProcessor.setInstance(new DataProcessor(null));
        assertNotNull(DataProcessor.getInstance());
    }

    /**
     * Makes sure we get an instance back after setting
     */
    @Test
    public void getInstance() {
        DataProcessor.setInstance(new DataProcessor(null));
        assertNotNull(DataProcessor.getInstance());
    }

    /**
     * Makes sure we get some activity back for RM after setting
     */
    @Test
    public void setActivity() {
        dp.setActivity(new Activity(), 1);
        assertNotNull(dp.getActivity(1));
    }

    /**
     * Makes sure we get some activity back for HP after setting
     */
    @Test
    public void getActivity() {
        dp.setActivity(new Activity(), 0);
        assertNotNull(dp.getActivity(0));
    }

    /**
     * Tests to make sure that changed days reflect those
     * changes
     */
    @Test
    public void retrieveDay() {
        // Get day and apply changes
        dp.insertDay(LocalDate.now());
        WalkDay day = dp.retrieveDay(LocalDate.now());
        day.setGoal(5000);
        day.setStepCountIntentional(40);
        day.setStepCountUnintentional(40);

        // Check if changes are correctly applied
        day = dp.retrieveDay(LocalDate.now());
        assertEquals(LocalDate.now(), day.getDate());
        assertEquals(5000, day.getGoal());
        assertEquals(40, day.getStepCountUnintentional());
        assertEquals(40, day.getStepCountIntentional());
        assertEquals(80, day.getStepCount());
    }

    /**
     * Tests to make sure that inserted days are 0-initialized.
     * Also ensures that they have the correct date.
     */
    @Test
    public void insertDay() {
        dp.insertDay(LocalDate.now());
        WalkDay day = dp.retrieveDay(LocalDate.now());
        assertEquals(0, day.getStepCount());
        assertEquals(0, day.getStepCountIntentional());
        assertEquals(0, day.getStepCountUnintentional());
        assertEquals(0, day.getGoal());
        assertEquals(LocalDate.now(), day.getDate());
    }

    @Test
    public void modifyDay() {
    }

    @Test
    public void writeToSharedPref() {
    }
}