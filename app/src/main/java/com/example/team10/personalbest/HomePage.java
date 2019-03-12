package com.example.team10.personalbest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;

import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;


import java.time.LocalDate;
import java.util.Observable;
import java.util.Observer;


public class HomePage extends AppCompatActivity{

    private final int RC_SIGN_IN = 1; //For Google Log-in Intent
    protected TextView step_text;
    protected TextView goal_text;

    private static final String TAG = "HomePage";
    private Mediator activityMediator;
    private AlertDialog newGoalDialog;

    // Used to authorize account with Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Add factory to have mock mediator
        activityMediator = new ActivityMediator(this);

        // Init app
        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(false);//FIXME enable when everthing else done

        // Get the shared instance for firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // Creating and setting home page text
        goal_text = findViewById(R.id.currentGoal);
        step_text = findViewById(R.id.stepsCount);

        // after pressing this button, switch to running mode
        Button run_button = findViewById(R.id.startButton);
        run_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view){
                launchRunning();
            }
        });

        //set_time_text = findViewById(R.id.set_time_text);
        Button time_forward_button = findViewById(R.id.mock_forward);
        time_forward_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityMediator.timeTravelForward();
                /*
                try {
                    int time_in_milli = Integer.parseInt(set_time_text.getText().toString());
                    // Store this variable accordingly FIXME

                } catch (Exception e) {
                    Toast.makeText(HomePage.this, "Please enter a valid number",
                            Toast.LENGTH_LONG).show();
                }
                */
            }
        });
        Button time_backward_button = findViewById(R.id.mock_back);
        time_backward_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityMediator.timeTravelBackward();
                /*
                try {
                    int time_in_milli = Integer.parseInt(set_time_text.getText().toString());
                    // store this var in new time.......
                    // ..........................

                } catch (Exception e) {
                    Toast.makeText(HomePage.this, "Please enter a valid number",
                            Toast.LENGTH_LONG).show();
                }
                */
            }
        });
        Button time_now_button = findViewById(R.id.mock_now);
        time_now_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityMediator.timeTravelNow();
                /*
                try {
                    int time_in_milli = Integer.parseInt(set_time_text.getText().toString());
                    // store this var in new time.......
                    // ..........................

                } catch (Exception e) {
                    Toast.makeText(HomePage.this, "Please enter a valid number",
                            Toast.LENGTH_LONG).show();
                }
                */
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

        //Friends List Button
        Button friends_list_button = findViewById(R.id.friendsListButton);
        friends_list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchFriendsList();
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
                activityMediator.mockStepInHP();
                //fit.passMockIntoRun();
            }
        });

        //load data into home page and call text view update methods

         /*
          * Log into Google Account:
          * Configure sign-in to request basic profile (included in DEFAULT_SIGN_IN)
          * https://developers.google.com/identity/sign-in/android/sign-in
          */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        //https://developers.google.com/identity/sign-in/android/sign-in
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //launches an activity that prompts sign in
        //https://developers.google.com/android/reference/com/google/android/gms/auth/api/signin/GoogleSignInClient
        Log.i(TAG, "About to send intent");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult( signInIntent, RC_SIGN_IN );
        Log.i(TAG, "Intent is sent");
    }

    /**
     * firebaseAuthWithGoogle
     *
     * Authenticate an account using firebase with google. Gets a credential
     * and signs in using it. If it is successful, we have a listener
     *
     * @param acct The account to authenticate with firebase
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success - may have to update UI. FIXME
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                        } else {
                            // If sign in fails, report in log.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    /**
     * launchRunning
     *
     * Launches the running mode activity
     */
    public void launchRunning() {
        Intent intent = new Intent(this, RunningMode.class);
        startActivity(intent);
    }

    public void launchBarChart() {
        activityMediator.saveLocal();
        Intent intent = new Intent(this, BarChartActivity.class);
        startActivity(intent);
    }

    public void launchFriendsList() {
        Intent intent = new Intent(this, FriendListPage.class);

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

    /**
     * onActivityResult
     *
     * Called after startActivityForResult() (called in onCreate()) is finished
     * Code from: https://developers.google.com/identity/sign-in/android/sign-in
     * After the user signs in, GoogleSignInAccount can be reached here.
     * Firebase authenticated on a successful login.
     *
     * @param requestCode   Request code used with the activity
     * @param resultCode    The code from the activity
     * @param data          Data associated with activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i( TAG, "Intent is done/closed");

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {



            // Obtain the account used to sign in and authenticate firebase
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                currentUser = firebaseAuth.getCurrentUser();


            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
            if(currentUser != null){
                activityMediator.setCurrentUser(currentUser);
                //Initialize the activity mediator.
                activityMediator.init();

                // Build the activity mediator.
                activityMediator.build();
                // Setup asynchronous tasks.
                Log.i(TAG, "Preparing to run Async Task");
                AsyncTaskRunner runner = new AsyncTaskRunner();
                runner.execute();
                Log.i(TAG, "Async Task is run");
            }

        }

    }

    public void openNewGoalDialog() {
        SharedPreferences goalPreferences = getSharedPreferences("goal_count", MODE_PRIVATE);

        AlertDialog.Builder newGoalBuilder = new AlertDialog.Builder(this);
        newGoalBuilder.setMessage(R.string.set_method);
        newGoalBuilder.setPositiveButton(getString(R.string.suggested) + " " + Integer.toString(goalPreferences.getInt("goalCount", 5000) + 500), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activityMediator.setGoal_today(activityMediator.getGoal_today()+500);
                TextView goal_text = findViewById(R.id.currentGoal);
                goal_text.setText(Integer.toString(activityMediator.getGoal_today()));

                activityMediator.setGoalMet(false);


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
                            activityMediator.setGoalMet(false);
                            activityMediator.setGoal_today(tooLow);


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
    public void showGoal(int Goal){
        goal_text.setText(Integer.toString(Goal));
    }

    public void showStepCount(int count){
        Log.i(TAG, "TextView is updated");
        step_text.setText(Integer.toString(count));
    }

    public void checkGoal() {
        if(activityMediator.checkReachGoal()&& !activityMediator.getGoalMet()) {
            Log.i(TAG, "Inside checkGoal");
            activityMediator.setGoalMet(true);
            openCongratsDialog();
        }
        Log.i(TAG, Boolean.toString(activityMediator.getGoalMet()));
        Log.i(TAG, "Step Count: " + Integer.toString(activityMediator.getStepCountDailyTotal()));
        Log.i(TAG, "Current Goal: " + Integer.toString(activityMediator.getGoal_today()));
    }

    private class AsyncTaskRunner extends AsyncTask<String,String,String> {
        @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
        @Override
        protected void onPreExecute(){
            activityMediator.setup();
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
        activityMediator.saveLocal();
        activityMediator.stop();
    }

    protected Mediator getTestMediator(){
        return activityMediator;
    }

    protected void setTestMediator(Mediator m){
        activityMediator = m;
    }
}
