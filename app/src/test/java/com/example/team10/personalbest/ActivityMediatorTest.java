package com.example.team10.personalbest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class ActivityMediatorTest {
    private ActivityMediator mediator;
    @Before
    public void setUp() throws Exception {
        mediator = new ActivityMediator();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void compute() {
        assertEquals(mediator.stepCountIntentionalTotal -mediator.stepCountIntentionalReal, mediator.mock_steps_intentional+mediator.mock_steps_run);
        assertEquals(mediator.stepCountUnintentionalTotal -mediator.stepCountUnintentionalReal, mediator.mock_steps_unintentional);
        assertEquals(mediator.stepCountDailyTotal-mediator.stepCountDailyReal, mediator.mock_steps_unintentional+mediator.mock_steps_run+mediator.mock_steps_intentional);
        assertEquals(mediator.stepCountDailyReal-mediator.stepCountUnintentionalReal, mediator.stepCountUnintentionalReal);
    }
}