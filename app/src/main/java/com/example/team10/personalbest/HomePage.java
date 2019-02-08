package com.example.team10.personalbest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class HomePage extends AppCompatActivity {

    private int currentGoal = 5000;
    private int stepCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        TextView goal_text = findViewById(R.id.currentGoal);
        goal_text.setText(Integer.toString(currentGoal));

        TextView step_text = findViewById(R.id.stepsCount);
        step_text.setText(Integer.toString(stepCount));

        Button run_button = findViewById(R.id.startButton);
        run_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view){
                launchRunning();
            }
        });

        ImageButton bar_chart_button = findViewById(R.id.barButton);
        bar_chart_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                launchBarChart();
            }
        });

        openCongratsDialog();


        SharedPreferences goalPreferences = getSharedPreferences("goal_count", MODE_PRIVATE);

        int updatedGoal = goalPreferences.getInt("goalCount", 5000);
        goal_text.setText(Integer.toString(updatedGoal));

    }

    public void launchRunning() {
        Intent intent = new Intent(this, RunningMode.class);
        startActivity(intent);
    }

    public void launchBarChart() {
        Intent intent = new Intent(this, BarChart.class);
        startActivity(intent);
    }

    public void openCongratsDialog() {
        AlertDialog.Builder congratsBuilder = new AlertDialog.Builder(this);
        congratsBuilder.setMessage(R.string.congrats_message);
        congratsBuilder.setPositiveButton(R.string.yes_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openNewGoalDialog();
            }
        });

        congratsBuilder.setNegativeButton(R.string.not_now, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog congratsDialog = congratsBuilder.create();
        congratsDialog.setCanceledOnTouchOutside(false);
        //if(currentGoal == stepCount) {
            congratsDialog.show();
        //}
    }

    public void openNewGoalDialog() {
        SharedPreferences goalPreferences = getSharedPreferences("goal_count", MODE_PRIVATE);

        AlertDialog.Builder newGoalBuilder = new AlertDialog.Builder(this);
        newGoalBuilder.setMessage(R.string.set_method);
        newGoalBuilder.setPositiveButton(getString(R.string.suggested) + " " + Integer.toString(goalPreferences.getInt("goalCount", 5000) + 500), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences goalPreferences = getSharedPreferences("goal_count", MODE_PRIVATE);
                SharedPreferences.Editor editor = goalPreferences.edit();
                currentGoal = goalPreferences.getInt("goalCount", 5000) + 500;
                TextView goal_text = findViewById(R.id.currentGoal);
                goal_text.setText(Integer.toString(currentGoal));

                editor.putInt("goalCount", Integer.parseInt(goal_text.getText().toString()));
                editor.apply();

                Toast.makeText(HomePage.this, R.string.goal_updated_toast, Toast.LENGTH_LONG).show();
            }
        });

        newGoalBuilder.setNegativeButton(R.string.custom, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //add custom goal functionality later
            }
        });
        AlertDialog newGoalDialog = newGoalBuilder.create();
        newGoalDialog.setCanceledOnTouchOutside(false);
        newGoalDialog.show();
    }
}
