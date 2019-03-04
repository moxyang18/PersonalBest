package com.example.team10.personalbest;

// Android dev packages
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

// File related
import com.google.gson.Gson;
import android.content.SharedPreferences;
import android.util.Log;

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
 *
 * Notes:
 *  -Instantiate the DP and setInstance at the start of the HomePage activity. This is
 *      necessary to ensure that we retrieve the data from SharedPrefs.
 *  -Insert a new day for today in HomePage when the app opens. This is needed to
 *      access it from the table later for modification and retrieval.
 */
public class DataProcessor extends AppCompatActivity {

    // These activities employ DP.
    private HomePage hp;                // NOTE: equivalent of activity in GFA.java
    private RunningMode rm;             // NOTE: equivalent of activity_2 in GFA.java

    // The instance of our DataProcessor
    private static DataProcessor INSTANCE;

    // Debug stuff
    String TAG = "DataProcessor - ";

    // Store dates mapping to days of walks
    protected Hashtable<String, WalkDay> table;

    // Allows for dates to be formatted as strings
    Gson gson;

    // SharedPref
    private static final String FILE_NAME = "STEP DATA";
    private static final String TABLE_NAME = "STEP TABLE";

    LocalDate date =LocalDate.now();

    /**
     * DataProcessor ctor
     *
     * Constructs a Data Processor for the given home activity.
     *
     * @param hp The HomePage activity.
     */
    public DataProcessor(HomePage hp) {
        // Access homepage
        this.hp = hp;
        setInstance(this);
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
        if(table == null) table = new Hashtable<>();
    }

    /**
     * setInstance
     *
     * Sets the instance of the class to be the passed in object.
     *
     * @param dp The DataProcessor instance
     * @return boolean True if instance is non-null, else false
     */
    public static boolean setInstance(DataProcessor dp){
        if (dp != null) {
            INSTANCE = dp;
            return true;
        } else return false;
    }

    /**
     * getInstance
     *
     * Returns the static instance of the DP. Allows the DP to
     * be accessed by multiple activities without having to pass it
     * to those activities.
     *
     * @return DataProcessor The DP.
     */
    public static DataProcessor getInstance(){
        return INSTANCE;
    }

    /**
     * retrieveDay
     *
     * Retrieve a WalkDay object from the table and return it.
     * !!!May return null!!!
     *
     * @param date The date we'll get the info for.
     * @return WalkDay The day associated with the date. May be null!
     */
    public WalkDay retrieveDay(LocalDate date) {
        return table.get(date.toString());
    }

    /**
     * insertDay
     *
     * Inserts a new day in the table. Writes to sharedPrefs.
     *
     * @param date The date.
     */
    public void insertDay(LocalDate date,WalkDay walkDay) {

        // Create a new day in the table.
        table.put(date.toString(), walkDay);
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
        editor.putString(TABLE_NAME ,gson.toJson(table));
        editor.apply();
    }

    public void retrieveFromCloud() {

    }

    public void updateCloud() {

    }
}
