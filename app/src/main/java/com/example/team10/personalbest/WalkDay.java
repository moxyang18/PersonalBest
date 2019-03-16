package com.example.team10.personalbest;
import java.time.LocalDate;

/**
 * WalkDay
 *
 * A simple class to store a steps associated with a date.
 */
public class WalkDay {
    private int stepCountIntentional =0;
    private int stepCountUnintentional =0;
    private int stepCountUnintentionalReal =0;
    private int stepCountIntentionalReal =0;
    private int mock_steps_unintentional =0;
    private int mock_steps_intentional =0;
    private int stepCountDailyReal =0;
    private int stepCountDailyTotal = 0;
    private int goal =5000;
    private boolean goalMet =false;
    private float distanceDaily =0.f;
    private float distanceRunTotal =0.f;
    private long time_run_sec_daily = 0;
    private float speed_average =0.f;
    private String date =LocalDate.now().toString();


    public WalkDay(){

    }

    /**
     * WalkDay ctor
     *
     * Creates a new WalkDay with a date. Everything else will
     * be 0-initialized.
     *
     * @param date The date
     */

    public WalkDay(String date) {
        this.date = date;
    }

    // SETTERS

    /**
     * setStepCountIntentional
     *
     * Sets the intentional steps taken during the day.
     * These steps come from using running mode.
     *
     * @param steps The steps walked on this day
     */
    public void setStepCountIntentional(int steps) {
        this.stepCountIntentional = steps;
        //this.setStepCountDailyReal(getStepCountIntentional() + getStepCountUnintentional());
    }

    /**
     * setStepCountUnintentional
     *
     * Sets the unintentional steps taken during the day.
     *
     * @param steps The steps walked on this day
     */
    public void setStepCountUnintentional(int steps) {
        this.stepCountUnintentional = steps;
        //this.setStepCountDailyReal(getStepCountIntentional() + getStepCountUnintentional());
    }

    /**
     * setStepCountDailyReal
     *
     * Sets the total step count for the day. Updates if goal is met.
     *
     * @param steps The total step count for the day.
     */
    public void setStepCountDailyReal(int steps) {
        this.stepCountDailyReal = steps;
        if (stepCountDailyReal >= goal) {
            goalMet = true;
        }
    }

    /**
     * setString1
     *
     * Sets the date
     *
     * @param date The date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * setDistanceDaily
     *
     * Set the distance walked for the day.
     *
     * @param distanceDaily The distance walked.
     */
    public void setDistanceDaily(float distanceDaily) { this.distanceDaily = distanceDaily; }


    /**
     * setSpeed_average
     *
     * Set the speed_average for the day. Should be average.
     *
     * @param speed_average The average speed_average for the day.
     */
    public void setSpeed_average(float speed_average) {this.speed_average = speed_average; }

    /**
     * setGoal
     *
     * Set the goal of the day.
     *
     * @param goal The goal of the day.
     */
    public void setGoal(int goal) { this.goal = goal; }

    // GETTERS

    /**
     * getStepCountIntentional
     *
     * Gets the steps taken this day
     *
     * @return int The steps taken this day
     */
    public int getStepCountIntentional() {
        return this.stepCountIntentional;
    }

    /**
     * getStepCountUnintentional
     *
     * Gets the steps taken this day
     *
     * @return int The steps taken this day
     */
    public int getStepCountUnintentional() {
        return this.stepCountUnintentional;
    }

    /**
     * getStepCountDailyReal
     *
     * Gets the total steps taken during the day
     *
     * @return int The total steps taken during the day
     */
    public int getStepCountDailyReal() { return this.stepCountDailyReal; }

    /**
     * getString1
     *
     * Gets the date of this day (LocalDate)
     *
     * @return String The date.
     */
    public String getDate() {
        return this.date;
    }

    /**
     * getDistanceDaily
     *
     * Returns the distance walked during the day.
     *
     * @return float The distance walked.
     */
    public float getDistanceDaily() { return this.distanceDaily; }

    /**
     * getSpeed_average
     *
     * Returns the speed_average during the day.
     *
     * @return float The average speed_average.
     */
    public float getSpeed_average() { return this.speed_average; }

    /**
     * getGoal
     *
     * Returns the goal of the day.
     *
     * @return int The goal of the day.
     */
    public int getGoal() { return this.goal; }

    public int getMock_steps_unintentional() {
        return mock_steps_unintentional;
    }

    public void setMock_steps_unintentional(int mock_steps_unintentional) {
        this.mock_steps_unintentional = mock_steps_unintentional;
    }

    public int getMock_steps_intentional() {
        return mock_steps_intentional;
    }

    public void setMock_steps_intentional(int mock_steps_intentional) {
        this.mock_steps_intentional = mock_steps_intentional;
    }

    public float getDistanceRunTotal() {
        return distanceRunTotal;
    }

    public void setDistanceRunTotal(float distanceRunTotal) {
        this.distanceRunTotal = distanceRunTotal;
    }

    public boolean getGoalMet() {
        return goalMet;
    }

    public void setGoalMet(boolean goalMet) {
        this.goalMet = goalMet;
    }

    public long getTime_run_sec_daily() {
        return time_run_sec_daily;
    }

    public void setTime_run_sec_daily(long time_run_sec_daily) {
        this.time_run_sec_daily = time_run_sec_daily;
    }

    public int getStepCountDailyTotal() {
        return stepCountDailyTotal;
    }

    public void setStepCountDailyTotal(int stepCountDailyTotal) {
        this.stepCountDailyTotal = stepCountDailyTotal;
    }

    public int getStepCountUnintentionalReal() {
        return stepCountUnintentionalReal;
    }

    public void setStepCountUnintentionalReal(int stepCountUnintentionalReal) {
        this.stepCountUnintentionalReal = stepCountUnintentionalReal;
    }

    public int getStepCountIntentionalReal() {
        return stepCountIntentionalReal;
    }

    public void setStepCountIntentionalReal(int stepCountIntentionalReal) {
        this.stepCountIntentionalReal = stepCountIntentionalReal;
    }


}
