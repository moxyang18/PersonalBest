package com.example.team10.personalbest;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class WalkDayTest {

    WalkDay day;

    @Before
    public void init() {
        day = new WalkDay(LocalDate.now().toString());
    }

    @Test
    public void setStepCountIntentional() {
        day.setStepCountIntentional(5);
        assertEquals(5, day.getStepCountIntentional());
    }

    @Test
    public void setStepCountUnintentional() {
        day.setStepCountUnintentional(5);
        assertEquals(5, day.getStepCountUnintentional());
    }

    @Test
    public void setStepCount() {
        day.setStepCountDailyReal(10);
        assertEquals(10, day.getStepCountDailyReal());
    }

    @Test
    public void setGoal() {
        day.setGoal(5000);
        assertEquals(5000, day.getGoal());
    }

    @Test
    public void getDate() {
        assertEquals(LocalDate.now(), day.getDate());
    }
}