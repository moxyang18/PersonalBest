package com.example.team10.personalbest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.team10.personalbest.fitness.FitnessService;
import com.example.team10.personalbest.fitness.FitnessServiceFactory;
import com.example.team10.personalbest.fitness.GoogleFitAdapter;

import java.util.Observable;
import java.util.Observer;

public class HomePage extends AppCompatActivity implements Observer {

    private int currentGoal = 5000;
    private int stepCount = 0;
    protected TextView step_text;
    protected TextView goal_text;

    //Include Fitness part
    private String fitnessServiceKey = "GOOGLE_FIT";
    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";
    private static final String TAG = "StepCountActivity";
    private GoogleFitAdapter fit;
    private FitnessService fitnessService;

    //

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


        /*
        * FitAdapter Initialize
        */
        FitnessServiceFactory.put(fitnessServiceKey, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(HomePage homePage) {
                return new GoogleFitAdapter(homePage);
            }
        });
        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);
        fit =((GoogleFitAdapter)fitnessService);
        fit.setup();

        //try to run Async Task since OnCreate
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();
        /*
        *
        * END OF FIT PART
        */


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
                openCustomDialog();
            }
        });
        AlertDialog newGoalDialog = newGoalBuilder.create();
        newGoalDialog.setCanceledOnTouchOutside(false);
        newGoalDialog.show();
    }

    public void openCustomDialog() {
        AlertDialog.Builder customBuilder = new AlertDialog.Builder(this);
        customBuilder.setMessage(R.string.custom_prompt);

        final EditText customField = new EditText(this);
        customField.setInputType(InputType.TYPE_CLASS_NUMBER);

        customBuilder.setView(customField);
        customBuilder.setPositiveButton(R.string.confirm_custom, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences goalPreferences = getSharedPreferences("goal_count", MODE_PRIVATE);
                SharedPreferences.Editor editor = goalPreferences.edit();
                //currentGoal = goalPreferences.getInt("goalCount", 5000) + 500;
                TextView goal_text = findViewById(R.id.currentGoal);

                //TODO: if there is no input, display a toast and prevent the user from moving forward
                goal_text.setText(customField.getText());

                editor.putInt("goalCount", Integer.parseInt(goal_text.getText().toString()));
                editor.apply();

                Toast.makeText(HomePage.this, R.string.goal_updated_toast, Toast.LENGTH_LONG).show();
            }
        });

        customBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        AlertDialog customDialog = customBuilder.create();
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.show();
    }

    /*Fitness Methods

    ***
    *
    *
    * /
    * */
    public void setFitnessServiceKey(String fitnessServiceKey) {
        this.fitnessServiceKey = fitnessServiceKey;
    }

    @Override
    public void update(Observable o, Object arg){
        setStepCount((int)arg);
        showStepCount();
    }

    public void setStepCount(int count){
        stepCount = count;
    }
    public void showStepCount(){
        step_text.setText(Integer.toString(stepCount));
    }

    private class AsyncTaskRunner extends AsyncTask<String,String,String> {

        private String index;
        @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
        @Override
        protected String doInBackground(String... paras){
            //publishProgress("Counting...");

            fit.startListen();

            return "";
        }

        @Override
        protected void onPostExecute(String result){
            //finalResult.setText(getString(R.string.ten));

        }
        @Override
        protected void onPreExecute(){

        }
        @Override
        protected void onProgressUpdate(String... text){

        }
    }

}
