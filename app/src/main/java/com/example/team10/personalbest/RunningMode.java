package com.example.team10.personalbest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.team10.personalbest.fitness.GoogleFitAdapter;


import java.sql.Array;
import java.time.LocalDate;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class RunningMode extends AppCompatActivity{
    private String TAG = "Running Mode ";

    //private GoogleFitAdapter fit;
    private Mediator activityMediator;
    private  DataProcessor dp;
    //private int goal = 0;
    /*
    private int stepCount =0;
    private int stepCountUnintentional =0;
    private int stepCountIntentional =0;

    private float speed = 0.f;
    private float distance = 0.f;
    private float dailyDistance = 0.f;

    */
    private TextView stepText;
    private TextView goalText;
    private TextView speedText;
    private TextView distanceText;
    private TextView intentionalStepText;
    private TextView timeText;


    private boolean hasStopped = false;

    public int mock_steps_unintentional =0;
    public int mock_steps_intentional = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_running_mode);

        // get the buttons we need to set the actions after pressed
        Button end_run_button = findViewById(R.id.end_run);
        Button back_button = findViewById(R.id.back_from_running);

        Button add_step_button = findViewById(R.id.add_steps_in_running);
        //final EditText set_time_text = findViewById(R.id.set_time_text);

        goalText= findViewById(R.id.goal_running_mode);
        timeText = findViewById(R.id.time);
        speedText =findViewById(R.id.cur_velocity);
        stepText = findViewById(R.id.total_steps_rm);
        distanceText = findViewById(R.id.cur_miles);
        intentionalStepText = findViewById(R.id.running_steps);

        activityMediator = ActivityMediator.getInstance();
        activityMediator.linkRunning(this);
        // when pressed, set a new time in milliseconds

        // if the end walk/run button gets pressed, stop updating vars on this page,
        // showing the encouragement, but do not go back yet
        end_run_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set the boolean value to false to stop updating
                activityMediator.unlinkRunning();
                String message;


            }
        });

        // after pressing this button, increment current steps by 500
        add_step_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityMediator.mockStepInRM();
            }
        });


        // if the back button is pressed, go back to the home page
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Condition: If numOfFriends == 0
                friendless_notification();

                finish();
            }
        });

    }

    // the legacy function that sends the message from the system automatically
    public void friendless_notification () {
        dp = DataProcessor.getInstance();

        String encourage_mes;
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        WalkDay todayData = dp.retrieveDay(today);
        WalkDay yesterdayData = dp.retrieveDay(yesterday);

        int todayTotals = (todayData==null) ? 0 :todayData.getStepCountDailyTotal();
        int yesterdayTotals = (yesterdayData==null) ? 0: yesterdayData.getStepCountDailyTotal();

        int increment = todayTotals - yesterdayTotals; //stepCount - yesterdayStepCount
        if (increment <= 0)
            encourage_mes = "Great! Keep up the work! ";
        else if (increment <= 100)
            encourage_mes = "Congratulations! You have reached today's goal!";
        else
            encourage_mes = "Awesome! You've increased your " +
                    "daily steps by " + increment + " steps.";

        // create the notification manager and the channel
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String id = "enc_message_chanel";
        CharSequence name = "encouragement";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel encChannel = new NotificationChannel(id, name, importance);
        encChannel.enableLights(true);
        notificationManager.createNotificationChannel(encChannel);

        // build the local message sender
        NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(this, id)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Notification From PersonalBest Team")
                .setContentText(encourage_mes);

        notificationManager.notify(1, notBuilder.build());
    }

    public void setFitAdaptor() {

    }

    public void showStepCount(int stepCount){
        Log.i(TAG, "Textview is updated");
        stepText.setText(Integer.toString(stepCount));

    }

    public void showStepCountIntentional(int stepCountIntentional){
        Log.i(TAG, "Textview is updated");
        intentionalStepText.setText(Integer.toString(stepCountIntentional));
    }

    public  void showDistance(float distance){
        distanceText.setText(String.format("%.3f",distance*0.000621371));//distance*0.000621371));//converted to miles
    }

    public void showSpeed(float speed){
        //Log.d( "Textview is updated");
        speedText.setText(String.format("%.2f",speed*2.236f));

    }

    public void showGoal(int g){
        goalText.setText(Integer.toString(g));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityMediator.unlinkRunning();
    }

    public void showTime(String s){
        timeText.setText(s);
    }
    public boolean reachGoal(){
        return activityMediator.checkReachGoal();
    }
}
