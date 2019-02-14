package com.example.team10.personalbest;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.team10.personalbest.fitness.GoogleFitAdapter;


import java.util.Observable;
import java.util.Observer;

public class RunningMode extends AppCompatActivity implements Observer {
    private GoogleFitAdapter fit;
    private long stepCount;
    private TextView stepText;
    private String TAG = "Running Mode ";
    private float speed;
    private TextView speedText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_running_mode);

        // get the buttons we need to set the actions after pressed
        Button end_run_button = findViewById(R.id.end_run);
        Button back_button = findViewById(R.id.back_from_running);


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


        stepText = findViewById(R.id.running_steps);


        fit = GoogleFitAdapter.getInstance();
        fit.addObserver(this);
        fit.setActivity(this,1);
        fit.updateStepCount();

    }


    @Override
    public void update(Observable o, Object arg){
        setStepCount((long)arg);
        showStepCount();
    }


    public void setStepCount(long count){
        stepCount = count;
    }
    public void showStepCount(){
        Log.d(TAG, "Textview is updated");
        stepText.setText(Long.toString(stepCount));

    }
    public void setSpeed(float s){
        speed = s;
    }
    public void showSpeed(){
        //Log.d( "Textview is updated");
        speedText.setText(Float.toString(speed));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fit.setActivity(null,1);
    }
}
