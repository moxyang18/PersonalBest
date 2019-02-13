package com.example.team10.personalbest.fitness;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.team10.personalbest.HomePage;
import com.example.team10.personalbest.RunningMode;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getDateInstance;


public class GoogleFitAdapter extends Observable implements FitnessService{
    private final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = System.identityHashCode(this) & 0xFFFF;
    private final String TAG = "GoogleFitAdapter";

    private HomePage activity;
    private RunningMode activity_2;
    private static GoogleFitAdapter INSTANCE;
    private GoogleApiClient mClient;
    private long startTime;


    public GoogleFitAdapter(){

    }
    public GoogleFitAdapter(HomePage activity) {
        this.activity = activity;
    }

    public static  void setInstance(GoogleFitAdapter f){
        INSTANCE = f;
    }

    public  static GoogleFitAdapter getInstance(){
        if(INSTANCE != null){
            return INSTANCE;
        }else{
            INSTANCE = new GoogleFitAdapter();
            return  INSTANCE;
        }
    }

    public void setActivity(Activity a, int i){
        if(i ==0) activity = (HomePage)a;
        else if (i ==1) activity_2 =(RunningMode)a;

    }
    public Activity getActivity(int i){
        if(i ==0) return (Activity)activity;
        else return (Activity)activity_2;
    }

    public void setup() {
        //Handles what we want from Fitness data later
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_SPEED,FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_SPEED_SUMMARY,FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_LOCATION_TRACK, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_LOCATION_SAMPLE,FitnessOptions.ACCESS_READ)
                .build();

        //Actually request permission from user
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(activity), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    activity, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(activity),
                    fitnessOptions);
        }
                    //updateStepCount();
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        startTime = cal.getTimeInMillis();
        startRecording(); //Record API
        startListen(); //Sensor API
        updateStepCount();
        Log.d(TAG, "End setup");
        final Handler mUpdater = new Handler();
        Runnable mUpdateView = new Runnable() {
            @Override
            public void run() {
                if(activity_2 != null) {
                    updateSpeed();
                }
                mUpdater.postDelayed(this, 1200);
            }
        };
        //mUpdateView.run();
    }
    public void startListen(){

        OnDataPointListener mListener =
                new OnDataPointListener() {
                    @Override
                    public void onDataPoint(DataPoint dataPoint) {
                            //If the Listener detects 1+ steps have been taken, update
                            int val =dataPoint.getValue(Field.FIELD_STEPS).asInt();
                            if(val != 0){
                                updateStepCount();
                            }
                            updateSpeed();
                    }
                };

        OnDataPointListener mListener2 = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                //updateSpeed();
            }
        };

        //Register Listener
        Fitness.getSensorsClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
                .add(
                        new SensorRequest.Builder()
                                //.setDataSource(dataSource) // Optional but recommended for custom data sets.
                                .setDataType(DataType.TYPE_STEP_COUNT_DELTA) // Can't be omitted.
                                .setSamplingRate(500, TimeUnit.MILLISECONDS)
                                .build(),
                        mListener)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i(TAG, "Listener registered!");
                                } else {
                                    Log.e(TAG, "Listener not registered.", task.getException());
                                }
                            }
                        });
        /*
        Fitness.getSensorsClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
                .add(
                        new SensorRequest.Builder()
                                //.setDataSource(dataSource) // Optional but recommended for custom data sets.
                                .setDataType(DataType.AGGREGATE_SPEED_SUMMARY) // Can't be omitted.
                                .setSamplingRate(200, TimeUnit.MILLISECONDS)
                                .build(),
                        mListener2)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i(TAG, "Listener2 registered!");
                                } else {
                                    Log.e(TAG, "Listener2 not registered.", task.getException());
                                }
                            }
                        });
                    */

    }
    private void startRecording() {
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (lastSignedInAccount == null) {
            Log.d(TAG, "No Google Sign In Account at startRecording()");
            return;
        }

        Fitness.getRecordingClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Successfully subscribed!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "There was a problem subscribing.");
                    }
                });

        Fitness.getRecordingClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
                .subscribe(DataType.AGGREGATE_SPEED_SUMMARY)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Successfully subscribed speed!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "There was a problem subscribing speed Agg.");
                    }
                });

        Fitness.getRecordingClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
                .subscribe(DataType.TYPE_SPEED)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Successfully subscribed speed!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "There was a problem subscribing speed.");
                    }
                });


    }


    /**
     * Reads the current daily step total, computed from midnight of the current day on the device's
     * current timezone.
     */
    public void updateSpeed(){
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (lastSignedInAccount == null) {
            return;
        }

        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        //cal.add(Calendar.WEEK_OF_YEAR, -1);
        //startTime = endTime - 4000;

        java.text.DateFormat dateFormat = getDateInstance();
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .setTimeRange( startTime, endTime, TimeUnit.MILLISECONDS)
                .read(DataType.AGGREGATE_SPEED_SUMMARY)
                .setLimit(20)
                .build();

        Task<DataReadResponse> response = Fitness.getHistoryClient(activity, GoogleSignIn.getLastSignedInAccount(activity)).readData(readRequest).addOnSuccessListener(new OnSuccessListener<DataReadResponse>()  {
            @Override
            public void onSuccess(DataReadResponse dataReadResponse) {
                List<DataSet> dataSets = dataReadResponse.getDataSets();
                float s =
                        dataSets.isEmpty()|| dataSets.get(0).isEmpty()
                                ? (float)0
                                : dataSets.get(0).getDataPoints().get(dataSets.get(0).getDataPoints().size()-1).getValue(Field.FIELD_AVERAGE).asFloat();
                RunningMode r = ((RunningMode)getActivity(1));
                //if(r !=null) {
                    r.setSpeed(s);
                    r.showSpeed();
                    Log.d(TAG, "Running is updated");
                    Log.d(TAG, "Speed real: " + s);
                //}
            }
        });
    }
    public void updateStepCount() {
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (lastSignedInAccount == null) {
            return;
        }

        //Use History API to grab the last stored "Daily Total", then sum that with val to get the
        //updated Daily Step Count value.
        Fitness.getHistoryClient(activity, lastSignedInAccount)
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {
                            @Override
                            public void onSuccess(DataSet dataSet) {
                                Log.d(TAG, dataSet.toString());
                                long total =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();

                                setChanged();
                                //notifyObservers(total); // notify HomePage and Running Mode
                                //currently only notify with total steps daily
                                //activity.setStepCount(total);
                                //activity.showStepCount();
                                //activity.checkGoal();

                                //activity.updateDebugging();
                                ((HomePage)getActivity(0)).setStepCount(total);
                                ((HomePage) getActivity(0)).showStepCount();
                                /*
                                RunningMode r = ((RunningMode)getActivity(1));
                                if(r !=null) {
                                    r.setStepCount(total);
                                    r.showStepCount();
                                    Log.d(TAG, "Running is updated");
                                }
                                */

                                Log.d(TAG, "Total steps: " + total);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "There was a problem getting the step count.", e);
                            }
                        });


        /*
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        SessionReadRequest readRequest = new SessionReadRequest.Builder()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .read(DataType.TYPE_SPEED)
                .setSessionName("test")
                .build();
        */
        /*
        Fitness.getHistoryClient(activity, lastSignedInAccount)
                .readDailyTotal(DataType.TYPE_SPEED)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {
                            @Override
                            public void onSuccess(DataSet dataSet) {
                                Log.d(TAG, dataSet.toString());
                                float t =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_AVERAGE).asFloat();

                                setChanged();
                                //notifyObservers(t); // notify HomePage and Running Mode
                                //currently only notify with total steps daily
                                //activity.setStepCount(total);
                                //activity.showStepCount();
                                //activity.checkGoal();

                                //activity.updateDebugging();

                                RunningMode r = ((RunningMode)getActivity(1));
                                if(r !=null) {
                                    r.setSpeed(t);
                                    r.showSpeed();
                                    Log.d(TAG, "Running is updated");
                                }

                                Log.d(TAG, "Speed: " + t);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "There was a problem getting the speed.", e);
                            }
                        });

        */






        /*
        Fitness.getHistoryClient(activity, lastSignedInAccount)
                .readDailyTotal(DataType.AGGREGATE_SPEED_SUMMARY)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {
                            @Override
                            public void onSuccess(DataSet dataSet) {
                                Log.d(TAG, dataSet.toString());
                                float t =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_SPEED).asFloat();

                                setChanged();
                                //notifyObservers(t); // notify HomePage and Running Mode
                                //currently only notify with total steps daily
                                //activity.setStepCount(total);
                                //activity.showStepCount();
                                //activity.checkGoal();

                                //activity.updateDebugging();

                                RunningMode r = ((RunningMode)getActivity(1));
                                if(r !=null) {
                                    r.setSpeed(t);
                                    r.showSpeed();
                                    Log.d(TAG, "Running is updated");
                                }

                                Log.d(TAG, "Speed Agg: " + t);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "There was a problem getting the speed Agg.", e);
                            }
                        });
                        */
    }


    @Override
    public int getRequestCode() {
        return GOOGLE_FIT_PERMISSIONS_REQUEST_CODE;
    }

}
