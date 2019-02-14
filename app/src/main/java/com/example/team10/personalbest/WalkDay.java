package com.example.team10.personalbest.fitness;
import java.time.LocalDate;

/**
 * WalkDay
 *
 * A simple class to store a steps associated with a date.
 */
public class WalkDay {
    private int steps;
    private LocalDate date;

    /**
     * WalkDay ctor
     *
     * Creates a new WalkDay with steps and a date
     * @param steps The steps walked on this day
     * @param date The date
     */
    public WalkDay(int steps, LocalDate date) {
        this.steps = steps;
        this.date = date;
    }

    /**
     * setSteps
     *
     * Sets the steps
     *
     * @param steps The steps walked on this day
     */
    public void setSteps(int steps) {
        this.steps = steps;
    }

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
     * getSteps
     *
     * Gets the steps taken this day
     *
     * @return int The steps taken this day
     */
    public int getSteps() {
        return this.steps;
    }

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
}
