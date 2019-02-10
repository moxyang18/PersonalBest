package com.example.team10.personalbest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    private long runCount;
    private TextView t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_running_mode);

        t= findViewById(R.id.testTextView);

        Button end_run_button = findViewById(R.id.end_run);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        end_run_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = 1; // steps taken/today's goal
                String message;
                if (i < 0.5)
                    message = "Great! Keep up the work! ";
                else if (i >= 0.5 && i < 1.0)
                    message = "Congratulations! Just a little bit more towards the goal!";
                else
                    message = "Awesome! You have reached today's goal!";

                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                finish();
            }
        });
        fit = GoogleFitAdapter.getInstance();
        fit.addObserver(this);
        fit.setActivity(this,1);
        fit.updateStepCount();

    }

    /*
    Fit part
     */
    @Override
    public void update(Observable o, Object arg){
        t.setText(Integer.toString((int)arg));
    }

    public void setStepCount(long count){
        runCount = count;
    }
    public void showStepCount(){
        //Log.d( "Textview is updated");
        t.setText(Long.toString(runCount));
    }

}
