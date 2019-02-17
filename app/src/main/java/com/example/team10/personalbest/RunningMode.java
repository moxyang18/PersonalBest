package com.example.team10.personalbest;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.team10.personalbest.fitness.GoogleFitAdapter;


import java.sql.Array;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class RunningMode extends AppCompatActivity implements Observer {
    private GoogleFitAdapter fit;
    private  DataProcessor dp;
    private int stepCount =0;
    private int stepCountUnintentional =0;
    private int stepCountIntentional =0;
    private TextView stepText;
    private String TAG = "Running Mode ";
    private float speed = 0.f;
    private float distance = 0.f;
    private float dailyDistance = 0.f;
    private TextView speedText;
    private TextView distanceText;
    private TextView intentionalStepText;
    private TextView timeText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_running_mode);

        // get the buttons we need to set the actions after pressed
        Button end_run_button = findViewById(R.id.end_run);
        Button back_button = findViewById(R.id.back_from_running);
        speedText =findViewById(R.id.cur_velocity);
        stepText = findViewById(R.id.total_steps_rm);
        distanceText = findViewById(R.id.cur_miles);
        intentionalStepText = findViewById(R.id.running_steps);
        // if the end walk/run button gets pressed, stop updating vars on this page,
        // showing the encouragement, but do not go back yet

        end_run_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message;
                int increment = -1;  // curSteps - maxSteps
                if (true)    //curSteps >= goal)
                    message = "Awesome! You have reached today's goal!";
                else if (increment <= 0)
                    message = "Great! Keep up the work! ";
                else
                    message = "Congratulations! You've increased your " +
                            "daily steps by " + increment + " steps.";

                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

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




        fit = GoogleFitAdapter.getInstance();
        fit.addObserver(this);
        fit.setActivity(this,1);
        setStepCountUnitentional(fit.getTodayStepTotal());



    }


    @Override
    public void update(Observable o, Object arg){

        setStepCountIntentional((int)arg);
        showStepCountIntentional();
        /*
        Object[] arr = (Object[])arg;

        setStepCount((int)arr[0]);
        setStepCountIntentional(stepCount-stepCountUnintentional);

        showStepCount();
        showStepCountIntentional();

        setDistance((float)arr[1]);
        showDistance();

        setSpeed((float)arr[3]);
        showSpeed();
        */

    }
    public void setStepCount(int count){
        stepCount = count;
    }
    public int getStepCount(){return stepCount;}
    public void showStepCount(){
        Log.d(TAG, "Textview is updated");
        stepText.setText(Integer.toString(stepCount));

    }

    public void setStepCountUnitentional(int count){
        stepCountUnintentional = count;
    }
    public int getStepCountUnintentional (){return stepCountUnintentional;}





    public float getDistance() { return distance; }
    public void setDistance(float d){distance =d;}
    public  void showDistance(){distanceText.setText(String.format("%.2f",distance));}

    public void setStepCountIntentional(int count){
        stepCountIntentional = count;
    }
    public int getStepCountIntentional (){return stepCountIntentional;}
    public void showStepCountIntentional(){
        Log.d(TAG, "Textview is updated");
        intentionalStepText.setText(Integer.toString(stepCount));

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
}
