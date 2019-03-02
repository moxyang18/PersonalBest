package com.example.team10.personalbest;

import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

public class ActivityMediator implements Observer {

    public static ActivityMediator instance;

    public ActivityMediator(){

    }

    private WalkDay walkDay;
    private RunningMode runningMode;
    private HomePage homePage;

    //steps
    protected int stepCountDailyReal = 0;
    protected int stepCountDailyTotal = 0;


    protected int stepCountUnintentionalReal =0;
    protected int stepCountUnintentionalTotal =0;

    protected int stepCountIntentionalBeforeRun = 0;
    protected int stepCountIntentionalReal = 0;
    protected int stepCountIntentionalTotal = 0;

    protected int stepRun = 0; //running mode

    public int mock_steps_unintentional =0;
    public int mock_steps_intentional =0;
    public int mock_steps_run = 0;

    public void compute(){

        stepCountDailyReal = stepCountIntentionalReal +stepCountUnintentionalReal;
        stepCountUnintentionalTotal = stepCountUnintentionalReal + mock_steps_unintentional;
        stepCountIntentionalReal = stepCountIntentionalBeforeRun+stepRun;
        stepCountIntentionalTotal = stepCountIntentionalReal +mock_steps_run+mock_steps_intentional;
        stepCountIntentionalTotal = stepCountIntentionalTotal+stepCountUnintentionalTotal;
    }

    //called after runningmode on destory
    public void cleanUpAfterRun(){
        mock_steps_intentional += mock_steps_run;
        mock_steps_run = 0;
    }

    private float distanceDailyTotal = 0.f;
    private float distanceRun = 0.f;
    private float speedRun = 0.f;

    //when time travel and past a day clear all daily datas

    private Date today;

    private long time_Now = 0;
    private long timeRun = 0;
    private long timeRunStart = 0;
    private long time_elapsed = 0;
    private int time_elapsed_sec_run = 0;
    private int time_elpased_sec_daily = 0;
    private String timeElapsed;





    private int stepCountRun = 0;

    private float average_speed =0.f;

    //public int mock_steps_intentional = 0;

    private float speed = 0.f;
    private float distance = 0.f;
    private float dailyDistance = 0.f;

    public String computeTimeElapsed(){
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        time_elapsed = cal.getTimeInMillis() - timeRunStart;
        //long millis = time_elapsed % 1000;
        long second = (time_elapsed / 1000) % 60;
        long minute = (time_elapsed / (1000 * 60)) % 60;
        long hour = (time_elapsed / (1000 * 60 * 60)) % 24;

        String time = String.format("%02d:%02d:%02d", hour, minute, second);
        return time;
    }

    @Override
    public void update(Observable observable, Object object){

    }

    public void updateRunningMode(){

    }

    public void updateHomePage(){

    }
}
