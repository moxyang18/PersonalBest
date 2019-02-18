package com.example.team10.personalbest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

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
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class RunningMode extends AppCompatActivity implements Observer {
    private String TAG = "Running Mode ";

    private GoogleFitAdapter fit;
    private  DataProcessor dp;
    private int goal = 0;
    private int stepCount =0;
    private int stepCountUnintentional =0;
    private int stepCountIntentional =0;

    private float speed = 0.f;
    private float distance = 0.f;
    private float dailyDistance = 0.f;

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


        Intent intent = getIntent();
        goal =Integer.valueOf(intent.getStringExtra("Goal_today"));
        setStepCountUnintentional(Integer.valueOf(intent.getStringExtra("Step_unintentional")));;
        setStepCount(stepCountUnintentional);
        setStepCountIntentional(stepCount-stepCountUnintentional);
        dailyDistance = Float.valueOf(intent.getStringExtra("Daily_distance"));

        // get the buttons we need to set the actions after pressed
        Button end_run_button = findViewById(R.id.end_run);
        Button back_button = findViewById(R.id.back_from_running);
        Button add_step_button = findViewById(R.id.add_steps_in_running);
        Button set_time_button = findViewById(R.id.set_time_in_running);
        final EditText set_time_text = findViewById(R.id.set_time_text);
        goalText= findViewById(R.id.goal_running_mode);
        timeText = findViewById(R.id.time);
        speedText =findViewById(R.id.cur_velocity);
        stepText = findViewById(R.id.total_steps_rm);
        distanceText = findViewById(R.id.cur_miles);
        intentionalStepText = findViewById(R.id.running_steps);

        // if pressed, increase the current steps by 500
        add_step_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // .................

            }
        });

        // when pressed, set a new time in milliseconds
        set_time_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int time_in_milli = Integer.parseInt(set_time_text.getText().toString());
                    // store this var in new time.......
                    // ..........................

                } catch (Exception e) {
                    Toast.makeText(RunningMode.this, "Please enter a valid number",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        // if the end walk/run button gets pressed, stop updating vars on this page,
        // showing the encouragement, but do not go back yet
        end_run_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set the boolean value to false to stop updating
                hasStopped = true;
                String message;
                int increment = 0;            //stepCount - yesterdayStepCount
                if (stepCount >= goal)
                    message = "Awesome! You have reached today's goal!";
                else if (increment <= 0)
                    message = "Great! Keep up the work! ";
                else
                    message = "Congratulations! You've increased your " +
                            "daily steps by " + increment + " steps.";
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

            }
        });

        // after pressing this button, increment current steps by 500
        Button add_step_button = findViewById(R.id.addStepButton_run);
        add_step_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mock_steps_intentional =500+mock_steps_intentional;
                setStepCountIntentional(stepCountIntentional);
                setStepCount(stepCount);
                showStepCount();
                showStepCountIntentional();
                fit.passMockIntoHome();
            }
        });

        // if the back button is pressed, go back to the home page
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dp = DataProcessor.getInstance();
        dp.setActivity(this,1);

        fit = GoogleFitAdapter.getInstance();
        fit.setStartTime();
        //setStepCountUnintentional(fit.getTodayStepTotal());
        //setStepCount(fit.getTodayStepTotal());
        //showStepCount();
        //goal = fit.getGoal();
        goalText.setText(Integer.toString(goal));

        showStepCount();
        showStepCountIntentional();
        fit.addObserver(this);
        fit.setActivity(this,1);
    }


    @Override
    public void update(Observable o, Object arg){

        //setStepCountIntentional((int)arg);
        //showStepCountIntentional();
        if(hasStopped == false) {
            Object[] arr = (Object[]) arg;

            setStepCount((int) arr[1]);
            setStepCountIntentional(stepCount - stepCountUnintentional);

            showStepCount();
            showStepCountIntentional();

            setDistance((float) arr[2] - dailyDistance);
            showDistance();

            updateSpeedByComp((float) arr[3]);
            //setSpeed((float)arr[3]);
            showSpeed();

            showTime((String) arr[4]);
        }
    }

    public void updateSpeedByComp(float time_elapsed){
        if(time_elapsed != 0.f) speed = distance*2.236f/time_elapsed;//converted to miles per hour
    }


    public void setStepCount(int count){
        if(count !=0) stepCount = count+mock_steps_unintentional+mock_steps_intentional;
    }
    public int getStepCount(){return stepCount;}
    public void showStepCount(){
        Log.d(TAG, "Textview is updated");
        stepText.setText(Integer.toString(stepCount));

    }

    public void setStepCountUnintentional(int count){
        stepCountUnintentional = count+mock_steps_unintentional;
    }
    public int getStepCountUnintentional (){return stepCountUnintentional;}

    public void setStepCountIntentional(int count){
        stepCountIntentional = count+mock_steps_intentional;
    }
    public int getStepCountIntentional (){return stepCountIntentional;}
    public void showStepCountIntentional(){
        Log.d(TAG, "Textview is updated");
        intentionalStepText.setText(Integer.toString(stepCountIntentional));

    }


    public float getDistance() { return distance; }
    public void setDistance(float d){distance =d;}
    public  void showDistance(){
        distanceText.setText(String.format("%.3f",distance*0.000621371));//converted to miles
    }



    public float getSpeed() { return speed; }
    public void setSpeed(float s){
        speed = s;
    }
    public void showSpeed(){
        //Log.d( "Textview is updated");
        speedText.setText(String.format("%.2f",speed));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fit.setActivity(null,1);
        fit.deleteObserver(this);
    }

    public void showTime(String s){
        timeText.setText(s);
    }
}
