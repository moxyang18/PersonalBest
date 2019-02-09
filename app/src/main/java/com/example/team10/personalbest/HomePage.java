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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;

import java.util.Observable;
import java.util.Observer;

public class HomePage extends AppCompatActivity implements Observer {

    private int currentGoal = 5000;
    private long stepCount = 0;
    private final int RC_SIGN_IN = 1; //For Google Log-in Intent

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

        goal_text = findViewById(R.id.currentGoal);
        goal_text.setText(Integer.toString(currentGoal));

        step_text = findViewById(R.id.stepsCount);
        step_text.setText(Long.toString(stepCount));

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

        /**
          * FitAdapter Initialize
          */
        /** Not sure how this works, maybe integrate later/ TODO
        FitnessServiceFactory.put(fitnessServiceKey, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(HomePage homePage) {
                return new GoogleFitAdapter(homePage);
            }
        });
        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);
        fit =((GoogleFitAdapter)fitnessService);
        */
        fit = new GoogleFitAdapter(this);
        //fit.setup();
        fit.addObserver(this);


        //try to run Async Task since OnCreate
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();

        /**
         * End of Fit Part
         */

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
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data); //do something with GoogleSignInAccount TODO delete later?

            Log.d(TAG, "Preparing to run Async Task");
            AsyncTaskRunner runner = new AsyncTaskRunner();
            runner.execute();
            Log.d(TAG, "Async Task is run");
        }
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
        Log.d(TAG, "Inside update()");
        setStepCount((long)arg);
        showStepCount();
    }

    public void setStepCount(long count){
        stepCount = count;
    }
    public void showStepCount(){
        Log.d(TAG, "Textview is updated");
        step_text.setText(Long.toString(stepCount));
    }

    private class AsyncTaskRunner extends AsyncTask<String,String,String> {

        private String index;
        @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
        @Override
        protected String doInBackground(String... paras){
            //publishProgress("Counting...");
            fit.setup();
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
