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

import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.example.team10.personalbest.fitness.GoogleFitAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;


import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Observable;
import java.util.Observer;

public class HomePage extends AppCompatActivity implements Observer {

    private int currentGoal = 5000;
    private int stepCount = 0;
    private int stepCountUnintentional =0;
    private int stepCountIntentional = 0;
    private float dailyDistanceCover = 0.f;
    public int mock_steps_unintentional =0;
    public int mock_steps_intentional = 0;

    private final int RC_SIGN_IN = 1; //For Google Log-in Intent

    private boolean goalMet = false;
    protected TextView step_text;
    protected TextView goal_text;
    protected EditText set_time_text ;

    private static final String TAG = "HomePage";

    public GoogleFitAdapter fit;
    private  DataProcessor dp;

    private AlertDialog newGoalDialog;

    public LocalDate date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        date = LocalDate.now();

        dp = new DataProcessor(this);//dp.loadIntoHomePage() implicitly called inside constructor.
        DataProcessor.setInstance(dp);

        goal_text = findViewById(R.id.currentGoal);
        goal_text.setText(Integer.toString(currentGoal));

        step_text = findViewById(R.id.stepsCount);
        step_text.setText(Integer.toString(stepCount));

        goalMet = false;

        // after pressing this button, switch to running mode
        Button run_button = findViewById(R.id.startButton);
        run_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view){
                launchRunning();
            }
        });
        set_time_text = findViewById(R.id.set_time_text);
        Button set_time_button = findViewById(R.id.set_time_in_hp);
        set_time_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    long time_in_milli = Long.valueOf(set_time_text.getText().toString());
                    // store this var in new time.......
                    date =
                            Instant.ofEpochMilli(time_in_milli).atZone(ZoneId.systemDefault()).toLocalDate();
                    passDate();
                    // ..........................

                } catch (Exception e) {
                    Toast.makeText(HomePage.this, "Please enter a valid number",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        // after pressing this button, dialog prompts for setting custom steps
        Button set_goal = findViewById(R.id.currentGoal);
        set_goal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                openCustomDialog();
            }
        });

        // after pressing this button, switch to bar chart interface
        ImageButton bar_chart_button = findViewById(R.id.barButton);
        bar_chart_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                launchBarChart();
            }
        });

        // after pressing this button, increment current steps by 500
        Button add_step_button = findViewById(R.id.addStepButton);
        add_step_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMock_steps_unintentional(500+mock_steps_unintentional);
                setStepCountUnintentional(stepCountUnintentional);
                setStepCount(stepCount);
                showStepCount();
                fit.passMockIntoRun();
            }
        });

        //SharedPreferences goalPreferences = getSharedPreferences("goal_count", MODE_PRIVATE);

        //currentGoal = goalPreferences.getInt("goalCount", 5000);
        goal_text.setText(Integer.toString(currentGoal));
        /** Log into Google Account:
         * Configure sign-in to request basic profile (included in DEFAULT_SIGN_IN)
         * https://developers.google.com/identity/sign-in/android/sign-in
         */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        //https://developers.google.com/identity/sign-in/android/sign-in
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //launches an activity that prompts sign in
        //https://developers.google.com/android/reference/com/google/android/gms/auth/api/signin/GoogleSignInClient
        Log.d(TAG, "About to send intent");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult( signInIntent, RC_SIGN_IN );
        Log.d(TAG, "Intent is sent");
    }

    public void setFitAdapter() {

    }

    public void launchRunning() {
        Intent intent = new Intent(this, RunningMode.class);
        intent.putExtra("Goal_today",Integer.toString(currentGoal));
        intent.putExtra("Step_unintentional",Integer.toString(stepCount));
        intent.putExtra("Daily_distance",Float.toString(dailyDistanceCover));
        fit.setGoal(currentGoal);
        startActivity(intent);
    }

    public void launchBarChart() {
        Intent intent = new Intent(this, BarChartActivity.class);

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
        congratsDialog.show();
    }

    //onActivityResult is called after startActivityForResult() (called in onCreate() ) is finished
    //Code from: https://developers.google.com/identity/sign-in/android/sign-in
    //After the user signs in, GoogleSignInAccount can be reached here.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d( TAG, "Intent is done/closed");

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            fit = new GoogleFitAdapter(this);
            GoogleFitAdapter.setInstance(fit);
            fit.addObserver(this);

            Log.d(TAG, "Preparing to run Async Task");
            AsyncTaskRunner runner = new AsyncTaskRunner();
            runner.execute();
            Log.d(TAG, "Async Task is run");
        }

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

                goalMet = false;


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
        newGoalDialog = newGoalBuilder.create();
        newGoalDialog.setCanceledOnTouchOutside(false);
        newGoalDialog.show();
    }

    public AlertDialog getNewGoalDialog() {
        return newGoalDialog;
    }

    public void openCustomDialog() {
        AlertDialog.Builder customBuilder = new AlertDialog.Builder(this);
        customBuilder.setMessage(R.string.custom_prompt);

        final EditText customField = new EditText(this);
        customField.setInputType(InputType.TYPE_CLASS_NUMBER);

        customField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});


        customBuilder.setView(customField);
        customBuilder.setPositiveButton(R.string.confirm_custom, null);


        customBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        final AlertDialog customDialog = customBuilder.create();
        customDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button confirmButton = customDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences goalPreferences = getSharedPreferences("goal_count", MODE_PRIVATE);
                        SharedPreferences.Editor editor = goalPreferences.edit();
                        TextView goal_text = findViewById(R.id.currentGoal);

                        String customInput = customField.getText().toString();
                        int tooLow = 0;

                        if(!customInput.isEmpty()) {
                            tooLow = Integer.parseInt(customInput);
                        }
                        if(!customInput.isEmpty() && tooLow < 10) {
                            Toast.makeText(HomePage.this, R.string.too_low, Toast.LENGTH_LONG).show();
                        }
                        if(customInput.isEmpty()) {
                            Toast.makeText(HomePage.this, R.string.custom_empty_prompt, Toast.LENGTH_LONG).show();
                        }
                        if(!customInput.isEmpty() && tooLow >= 10){
                            goal_text.setText(customField.getText());

                            goalMet = false;

                            currentGoal = tooLow;

                            editor.putInt("goalCount", Integer.parseInt(goal_text.getText().toString()));
                            editor.apply();

                            Toast.makeText(HomePage.this, R.string.goal_updated_toast, Toast.LENGTH_LONG).show();

                            customDialog.dismiss();
                        }
                    }
                });
            }
        });

        customDialog.setCanceledOnTouchOutside(false);
        customDialog.show();
    }

    @Override
    public void update(Observable o, Object arg){
        Log.d(TAG, "Inside update()");

        Object[] arr = (Object[])arg;

        setStepCount((int)arr[1]);
        setStepCountIntentional(stepCount-stepCountUnintentional);
        setStepCountUnintentional(stepCount-stepCountIntentional);
        showStepCount();
        setDistance((float)arr[2]);

        if((boolean)arr[0] == true){
            passDate();
            dp.modifyDay(0);
            dp.writeToSharedPref();
        }
        checkGoal();
    }

    public void setMock_steps_unintentional(int c){mock_steps_unintentional =c;}
    public void setMock_steps_intentional (int c){mock_steps_intentional =c;}

    public void setStepCount(int count){
        stepCount = count+mock_steps_unintentional+mock_steps_intentional;
    }

    public int getStepCount(){ return stepCount;}

    public void setStepCountUnintentional(int count){stepCountUnintentional = count+mock_steps_unintentional;}
    public int getStepCountUnintentional(){return stepCountUnintentional;}

    public int getStepCountIntentional(){return stepCountIntentional;}
    public void setStepCountIntentional(int s){stepCountIntentional = s+mock_steps_intentional;}

    public void setDistance(float d){dailyDistanceCover = d;}
    public float getDistance(){return dailyDistanceCover;}

    public int getGoal(){return  currentGoal;}
    public void setGoal(int g){currentGoal = g;}

    public void showStepCount(){
        Log.d(TAG, "TextView is updated");
        step_text.setText(Integer.toString(stepCount));
    }

    public void checkGoal() {
        if(stepCount >= currentGoal && !goalMet) {
            Log.d(TAG, "Inside checkGoal");
            goalMet = true;
            openCongratsDialog();
        }
        Log.d(TAG, Boolean.toString(goalMet));
        Log.d(TAG, "Step Count: " + Integer.toString(stepCount));
        Log.d(TAG, "Current GOal: " + Integer.toString(currentGoal));
    }


    private class AsyncTaskRunner extends AsyncTask<String,String,String> {
        @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
        @Override
        protected void onPreExecute(){
            fit.setup();
        }
        @Override
        protected String doInBackground(String... paras){

            return "";
        }

        @Override
        protected void onPostExecute(String result){

        }

        @Override
        protected void onProgressUpdate(String... text){

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        passDate();
        dp.modifyDay(0);
        dp.writeToSharedPref();
    }

    public void passDate(){
        dp.date = date;
    }
}
