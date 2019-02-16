package com.example.team10.personalbest;
import java.time.LocalDate;

/**
 * WalkDay
 *
 * A simple class to store a steps associated with a date.
 */
public class WalkDay {
    private int stepCountIntentional;
    private int stepCountUnintentional;
    private int stepCount;
    private float dist;
    private float speed;
    private LocalDate date;

    /**
     * WalkDay ctor
     *
     * Creates a new WalkDay with a date. Everything else will
     * be 0-initialized.
     *
     * @param date The date
     */
    public WalkDay(LocalDate date) {
        this.date = date;

        // 0-initialize
        this.stepCountIntentional = 0;
        this.stepCountUnintentional = 0;
        this.stepCount = 0;
        this.dist = 0;
        this.speed = 0;
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
        this.stepCount = getStepCountIntentional() + getStepCountUnintentional();
    }

    /**
     * setStepCountUnintentional
     *
     * Sets the unintentional steps taken during the day.
     *
     * @param steps The steps walked on this day
     */
    public void setStepCountUnntentional(int steps) {
        this.stepCountUnintentional = steps;
        this.stepCount = getStepCountIntentional() + getStepCountUnintentional();
    }

    /**
     * setStepCount
     *
     * Sets the total step count for the day.
     *
     * @param steps The total step count for the day.
     */
    public void setStepCount(int steps) { this.stepCount = steps; }

    /**
     * setDate
     *
     * Sets the date
     *
     * @param date The date
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * setDist
     *
     * Set the distance walked for the day.
     *
     * @param dist The distance walked.
     */
    public void setDist(float dist) { this.dist = dist; }

    /**
     * setSpeed
     *
     * Set the speed for the day. Should be average.
     *
     * @param speed The average speed for the day.
     */
    public void setSpeed(float speed) {this.speed = speed; }

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
     * getStepCount
     *
     * Gets the total steps taken during the day
     *
     * @return int The total steps taken during the day
     */
    public int getStepCount() { return this.stepCount; }

    /**
     * getDate
     *
     * Gets the date of this day (LocalDate)
     *
     * @return LocalDate The date.
     */
    public LocalDate getDate() {
        return this.date;
    }

    /**
     * getDist
     *
     * Returns the distance walked during the day.
     *
     * @return The distance walked.
     */
    public float getDist() { return this.dist; }

    /**
     * getSpeed
     *
     * Returns the speed during the day.
     *
     * @return The average speed.
     */
    public float getSpeed() { return this.speed; }
}
