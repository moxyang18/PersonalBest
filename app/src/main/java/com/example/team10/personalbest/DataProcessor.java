package com.example.team10.personalbest;

// Android dev packages
import android.support.v7.app.AppCompatActivity;

// File related
import com.example.team10.personalbest.fitness.WalkDay;
import com.google.gson.Gson;
import android.content.SharedPreferences;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

// Java packages
import java.util.Hashtable;
import java.time.LocalDate;

/**
 * DataProcessor
 *
 * A class that stores a daily step data into a hahstable.
 * This hashtable, whenever modified, is submitted to SharedPreferences as a json string.
 * Upon instantiation, this dataprocessor will load in the hashtable from
 * SharedPreferences.
 */
public class DataProcessor extends AppCompatActivity {
    private HomePage hp;

    // Debug stuff
    String TAG = "DataProcessor - ";

    // Store dates mapping to days of walks
    Hashtable<String, WalkDay> table;

    // Allows for dates to be formatted as strings
    Gson gson;

    // SharedPref
    private static final String FILE_NAME = "STEP DATA";
    private static final String TABLE_NAME = "STEP TABLE";

    /**
     * Constructs a Data Processor for the given home activity.
     *
     * @param hp The HomePage activity.
     */
    public DataProcessor(HomePage hp) {
        // Access homepage
        this.hp = hp;

        // Access gson to convert map to json string
        gson = new Gson();

        /*
         * Just in case the table doesn't exist yet, we'll initialize one.
         * Notice how the second parameter to fromJson below is "gson.toJson(table)".
         * We're sending in a json representation of the empty hashtable as our
         * default value if the preference doesn't exist in sharedpref.
         */
        table = new Hashtable<>();

        // Get sharedprefs
        SharedPreferences prefs = hp.getSharedPreferences(FILE_NAME, MODE_PRIVATE);

        // Obtain the type of our map
        Type type = new TypeToken< Hashtable<String, WalkDay>>(){}.getType();

        // Use sharedprefs + the type token to retrieve our map if necessary
        table = gson.fromJson(prefs.getString(TABLE_NAME, gson.toJson(table)), type);
    }

    /**
     * retrieveDaySteps
     *
     * Retrieves the steps for the day based on a date.
     *
     * @param date The date.
     * @return int The steps read in for the time interval.
     */
    public int retrieveDaySteps(LocalDate date) {
        WalkDay walkDay = table.get(date.toString());

        // Handle if no step data exists for that date
        if (walkDay != null) {
            return walkDay.getSteps();
        } else {
            return 0;
        }
    }

    /**
     * retrieveDaySteps
     *
     * Retrieves the steps for the day based on a day, month and year.
     *
     * @param day The day of month as int.
     * @param month The month of year as int.
     * @param year The year as int.
     * @return The steps walked during this day.
     */
    public int retrieveDaySteps(int day, int month, int year) {
        LocalDate date = LocalDate.of(year, month, day);
        WalkDay walkDay = table.get(date.toString());

        // Handle if no step data exists for that date
        if (walkDay != null) {
            return walkDay.getSteps();
        } else {
            return 0;
        }
    }

    /**
     * insertDaySteps
     *
     * Inserts the steps for a day based on a date.
     *
     * @param date The date.
     * @param steps Steps taken on this day.
     */
    public void insertDaySteps(LocalDate date, int steps) {

        // Store the data
        table.put(date.toString(), new WalkDay(steps, date));
        writeToSharedPref();
    }

    /**
     * insertDaySteps
     *
     * Inserts the steps for a day based on a day, month, and year.
     *
     * @param day The day of the month as int.
     * @param month The day of the month as int.
     * @param year The day of the year as int.
     * @param steps Steps taken on this day.
     */
    public void insertDaySteps(int day, int month, int year, int steps) {
        LocalDate date = LocalDate.of(year, month, day);

        // Store the data
        table.put(date.toString(), new WalkDay(steps, date));
        writeToSharedPref();
    }

    /**
     * writeToSharedPref
     *
     * Submits the table to SharedPreferences for storage. Should be called by all
     * insertion methods in this class.
     */
    public void writeToSharedPref() {

        // Obtain the editor
        SharedPreferences prefs = hp.getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Store the data
        editor.putString(TABLE_NAME,gson.toJson(table));
        editor.apply();
    }
}
