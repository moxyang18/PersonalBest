package com.example.team10.personalbest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.time.LocalDate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class DataProcessorTest {

    DataProcessor dp;
    HomePage hp;

    @Before
    public void init() {
        hp = Robolectric.setupActivity(HomePage.class);
        dp = DataProcessor.getInstance();
    }

    /**
     *  Tests for when no day exists and when a day does exist.
     */
    @Test
    public void loadIntoHomePage() {
        // Before data exists for today, only 0-initialized data should be loaded in.
        dp.loadIntoHomePage();
        assertEquals(0, hp.getStepCountUnintentional());
        assertEquals(0, hp.getStepCount());

        // Modify the step data for today.
        WalkDay day = dp.retrieveDay(LocalDate.now());
        day.setStepCount(90);
        day.setStepCountUnintentional(90);
        dp.loadIntoHomePage();

        // See if the changes are reflected in HP after we load again.
        assertEquals(90, hp.getStepCountUnintentional());
        assertEquals(90, hp.getStepCount());
    }

    /**
     * Makes sure that the dp is set
     */
    @Test
    public void setInstance() {
        assertTrue(DataProcessor.setInstance(dp));
    }

    /**
     * Makes sure we get an instance back after setting
     */
    @Test
    public void getInstance() {
        assertNotNull(DataProcessor.getInstance());
    }

    /**
     * Makes sure we get some activity back for RM after setting
     */
    @Test
    public void setActivity() {
        dp.setActivity(hp, 0);
        assertNotNull(dp.getActivity(0));
    }

    /**
     * Makes sure we get some activity back for RM after setting
     */
    @Test
    public void getActivity() {
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