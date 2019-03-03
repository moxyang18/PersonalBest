package com.example.team10.personalbest;

import android.util.Log;

import com.example.team10.personalbest.fitness.GoogleFitAdapter;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

public class ActivityMediator implements Observer, Mediator {

    protected static ActivityMediator instance;



    static private String TAG = "Activity Mediator";

    protected LocalDate date = LocalDate.now();
    private WalkDay walkDay;
    private RunningMode runningMode;
    private HomePage homePage;
    private DataProcessor dataProcessor;
    private GoogleFitAdapter fit;

    protected int goal_today =0;
    protected boolean goalMet =false;
    //steps
    protected int stepCountDailyReal = 0;
    protected int stepCountDailyTotal = 0;


    protected int stepCountUnintentionalReal =0;
    protected int stepCountUnintentionalTotal =0;

    protected int stepCountIntentionalBeforeRun = 0;
    protected int stepCountIntentionalReal = 0;
    protected int stepCountIntentionalTotal = 0;
    protected int stepCountRunWithMock = 0;
    protected int stepCountRun = 0;


    protected int step_last_read = 0;
    protected int step_delta = 0;
    protected int un_delta = 0;
    protected int in_delta =0;

    //protected int stepRun = 0; //running mode

    public int mock_steps_unintentional =0;
    public int mock_steps_intentional =0;
    public int mock_steps_run = 0;

    private boolean isRunning = false;
    private float average_speed =0.f;
    private float speed = 0.f;
    private float distanceDailyTotal = 0.f;
    private float distanceRun = 0.f;
    private float distanceRunTotal =0.f;
    protected float distance_delta = 0.f;

    //when time travel and past a day clear all daily datas

    private LocalDate today = LocalDate.now();// not widely used

    private long time_Now = 0;
    private long timeRun = 0;
    private long timeRunStart = 0;
    private long time_elapsed = 0;
    private long time_elapsed_sec_run = 0;
    private long time_elpased_sec_daily = 0;
    private String timeElapsedStr ="00:00:00";
    protected boolean timeTraveled = false;


    public ActivityMediator(HomePage hp){
        homePage = hp;
        instance = this;
        dataProcessor = new DataProcessor(homePage);
    }

    protected static ActivityMediator getInstance(){
        return instance;
    }



    public void init(){
        walkDay = dataProcessor.retrieveDay(date);
        if (walkDay ==null){
            walkDay = new WalkDay();
            dataProcessor.insertDay(LocalDate.now(),walkDay);//writeToSharef implicitly called
            Log.d(TAG,"start with a new WalkDay ");
        }


        goal_today = walkDay.getGoal();
        goalMet = walkDay.getGoalMet();
        stepCountDailyReal = walkDay.getStepCountDailyReal();
        stepCountIntentionalReal = walkDay.getStepCountIntentionalReal();
        stepCountUnintentionalReal = walkDay.getStepCountUnintentionalReal();
        mock_steps_unintentional = walkDay.getMock_steps_unintentional();
        mock_steps_intentional = walkDay.getMock_steps_intentional();

        distanceDailyTotal =walkDay.getDistanceDaily();
        distanceRunTotal = walkDay.getDistanceRunTotal();
        time_elpased_sec_daily = walkDay.getTime_run_sec_daily();

        //not using direct read
        //stepCountIntentionalTotal =walkDay.getStepCountIntentional();
        //stepCountUnintentionalTotal = walkDay.getStepCountUnintentional();
        //stepCountDailyTotal = walkDay.getStepCountDailyTotal();

        //totals (3) are computed
        //run hasn't start yet
        stepCountDailyTotal = stepCountDailyReal+mock_steps_intentional+mock_steps_unintentional;
        stepCountIntentionalBeforeRun = stepCountIntentionalReal;
        stepCountIntentionalTotal = stepCountIntentionalReal + mock_steps_intentional;
        stepCountUnintentionalTotal = stepCountUnintentionalReal +mock_steps_unintentional;

        if(stepCountDailyTotal-stepCountDailyReal-mock_steps_unintentional-mock_steps_intentional >4 )
            Log.d(TAG,"Incorrect data computed for init");
        if(walkDay.getStepCountIntentional()-stepCountIntentionalTotal <-2 ||walkDay.getStepCountIntentional()-stepCountIntentionalTotal >2 )
            Log.d(TAG,"Incorrect data computed for init");
        updateHomePage();
        Log.d(TAG,"loaded today's data from shRef into HomePage");


    }


    public void computeStep(){
        // assert stepCountDailyReal = stepCountIntentionalReal +stepCountUnintentionalReal;
        stepCountUnintentionalReal += un_delta;
        stepCountUnintentionalTotal = stepCountUnintentionalReal + mock_steps_unintentional;
        stepCountRun +=in_delta;
        stepCountIntentionalReal = stepCountIntentionalBeforeRun+stepCountRun;
        stepCountRunWithMock = stepCountRun + mock_steps_run;
        stepCountIntentionalTotal = stepCountIntentionalReal +mock_steps_run+mock_steps_intentional;
        stepCountDailyTotal = stepCountIntentionalTotal+stepCountUnintentionalTotal;

        //System.out.println("Step Unin is now: "+stepCountUnintentionalTotal);

    }

    public void computeStats(){
        timeElapsedStr =computeTimeElapsed();
        distanceRunTotal+=distance_delta;
        distanceRun += distance_delta;
        speed = distanceRun/time_elapsed_sec_run;
        average_speed =
                (time_elpased_sec_daily == 0.f)
                        ? 0
                        : (distanceRunTotal / time_elpased_sec_daily);
    }





    public String computeTimeElapsed(){
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        time_elapsed = cal.getTimeInMillis() - timeRunStart;
        //long millis = time_elapsed % 1000;
        long second = (time_elapsed / 1000) % 60;
        long minute = (time_elapsed / (1000 * 60)) % 60;
        long hour = (time_elapsed / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }//timeRunStart only initialized to non null if linkRun

    @Override
    public void update(Observable observable, Object object){

        //handles date changes
        if (LocalDate.now()!=date &&!timeTraveled){
            if(isRunning) runningMode.finish();
            date = LocalDate.now();
            init();
        }

        Object[] arr = (Object[]) object;
        step_delta = (int)arr[1] - stepCountDailyReal; //needs to handle when past midnight
        stepCountDailyReal = (int)arr[1];
        distance_delta = (float)arr[2] - distanceDailyTotal;
        distanceDailyTotal = (float)arr[2];
        if(!isRunning){
            un_delta = step_delta;
            computeStep();
            computeStats();
        }
        else{
            in_delta = step_delta;
            computeStep();
        }
        //System.out.println("un_delta is now: "+ un_delta);
        un_delta = 0;
        in_delta = 0;
        step_delta =0;
        distance_delta = 0;
        updateHomePage();
        if(isRunning) updateRunningMode();

        if((boolean)arr[0] == true){
            saveLocal();
        }
    }

    public void updateRunningMode(){
        runningMode.showStepCount(stepCountDailyTotal);
        runningMode.showStepCountIntentional(stepCountRunWithMock);
        runningMode.showDistance(distanceRun);
        runningMode.showSpeed(speed);
        runningMode.showTime(timeElapsedStr);
    }

    public void updateHomePage(){
        homePage.showGoal(goal_today);
        homePage.showStepCount(stepCountDailyTotal);
        homePage.checkGoal();
    }

    public boolean checkReachGoal(){
        return stepCountDailyTotal >= goal_today;
    }

    //called by unlink
    public void cleanUpAfterRun(){
        mock_steps_intentional += mock_steps_run;
        mock_steps_run = 0;
        stepCountIntentionalReal +=stepCountRun;
        stepCountRun =0;
        stepCountIntentionalBeforeRun = stepCountIntentionalReal;
        stepCountIntentionalTotal = stepCountIntentionalReal + mock_steps_intentional;

        stepCountDailyTotal = stepCountUnintentionalTotal+stepCountIntentionalTotal;
        if(stepCountDailyTotal-stepCountDailyReal-mock_steps_unintentional-mock_steps_intentional >4 )
            Log.d(TAG,"Incorrect data computed for cleanUpAfterRun");

        updateHomePage();
        time_elpased_sec_daily += time_elapsed_sec_run;///needs modification
        time_elapsed_sec_run = 0;
        distanceRunTotal +=distanceRun;
        distanceRun =0;
        average_speed =
                (time_elpased_sec_daily == 0.f)
                ? 0
                : (distanceRunTotal / time_elpased_sec_daily);

        saveLocal();
    }

    public void unlinkRunning(){
        runningMode = null;
        isRunning = false;
        cleanUpAfterRun();
    }


    //called onDestroy
    protected void linkRunning(RunningMode rm){
        runningMode = rm;
        stepCountIntentionalBeforeRun =stepCountIntentionalReal;
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        timeRunStart = cal.getTimeInMillis();
        isRunning =true;
        updateRunningMode();
    }

    public void saveLocal(){
        if(!timeTraveled){
            walkDay.setStepCountUnintentionalReal(stepCountUnintentionalReal);
            walkDay.setStepCountUnintentional(stepCountUnintentionalTotal);//save but doesn't read in init
            walkDay.setStepCountIntentionalReal(stepCountIntentionalReal);
            walkDay.setStepCountIntentional(stepCountIntentionalTotal);//save but doesn't read in init
            walkDay.setStepCountDailyReal(stepCountDailyReal);
            walkDay.setStepCountDailyTotal(stepCountDailyTotal);//save but doesn't read in init
            walkDay.setDistanceDaily(distanceDailyTotal);
            walkDay.setDistanceRunTotal(distanceRunTotal);
            walkDay.setTime_run_sec_daily(time_elpased_sec_daily);
            walkDay.setSpeed_average(average_speed);//save but doesn't read in init
            walkDay.setMock_steps_intentional(mock_steps_intentional+mock_steps_run);
            walkDay.setMock_steps_unintentional(mock_steps_unintentional);
            walkDay.setGoal(goal_today);
            walkDay.setGoalMet(goalMet);
            dataProcessor.insertDay(date,walkDay);
        }

    }

    public void build(){
        fit = new GoogleFitAdapter(homePage);
        GoogleFitAdapter.setInstance(fit);
        fit.addObserver(this);
    }

    public void stop(){
        fit.deleteObserver(this);
    }

    public void setup(){
        fit.setup();
    }



    public void mockStepInHP(){
        mock_steps_unintentional += 500;
        computeStep();
        updateHomePage();
    }

    public void mockStepInRM(){
        mock_steps_run += 500;
        computeStep();
        updateRunningMode();
    }

    public int getGoal_today(){
        return goal_today;
    }

    public void setGoal_today(int g){
        goal_today = g;
    }

    public void setGoalMet(boolean m){
        goalMet =m;
    }

    public boolean getGoalMet(){
        return goalMet;
    }

    public int getStepCountDailyTotal(){
        return stepCountDailyTotal;
    }

    public void resetDay(){
        walkDay = new WalkDay();
        dataProcessor.insertDay(LocalDate.now(),walkDay);//writeToSharef implicitly called
        init();
    }

}