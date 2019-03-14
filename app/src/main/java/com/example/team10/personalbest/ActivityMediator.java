package com.example.team10.personalbest;

import android.app.Person;
import android.util.Log;

import com.example.team10.personalbest.fitness.CloudProcessor;
import com.example.team10.personalbest.fitness.GoogleFitAdapter;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;

import androidx.annotation.NonNull;

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
    private long timeRunMilli = 0;
    private long timeRunStart = 0;
    //private long time_elapsed = 0;
    private long time_elapsed_sec_run = 0;
    private long time_elapsed_sec_daily = 0;
    private String timeElapsedStr ="00:00:00";
    protected boolean timeTraveled = false;
    private FirebaseUser currentUser;
    private String userEmail;
    private String userDisplayName;
    PersonalBestUser personalBestUser;

    public ActivityMediator(HomePage hp){
        homePage = hp;
        instance = this;
        //dataProcessor = new DataProcessor(homePage);
    }

    protected static ActivityMediator getInstance(){
        return instance;
    }


    //we don't want to call sync too often, only upon potential difference between the local cache and cloud storage
    //we call sync whenever we reload the application
    public boolean sync(){
        if(CloudProcessor.checkExistingUserData(userEmail)){ //if not first time using the app

            //then check last upload date
            LocalDate lastInCloud = CloudProcessor.getLastUploadDate(userEmail);
            //LocalDate today = LocalDate.now();

            //FIXME Question: for none firstime condition, we have write stack which store each uploads,
            //would we really lose anything except broke the phone?
            if(lastInCloud.isBefore(LocalDate.now())){
                //DO SOME THING

                //WE PROBABLY BORKE THE PHONE !!!
                //BUT WE SHOULD HAVE THINGS SAVED BECAUSE WE ARE ONLY WRITING TO FIREBASE
                //IF WE WANT TO MERGE/HANLDE OVERLAP, IT MUST BE today because we would call sync
                //many times and the lastUploadDate in write stack should been changed afterwards
                //Then we just upload today
                if(walkDay ==null)
                    walkDay = new WalkDay(LocalDate.now().toString());

                CloudProcessor.uploadWalkDay(walkDay,userEmail);
                //need to update friend list;
                CloudProcessor.setLastUploadDate(LocalDate.now(),userEmail);
            }

             //FIXME if we want to do actuall merge.
            //if same day, we only do things if walkDay currently is bad.
             else if(lastInCloud.isAfter(LocalDate.now())){ //couldn't happen

            }else {//same day
                //lost phone today and get connected or we are just using the app
                //we can only get the day from cloud. can't rewrite it. since we don't actually local storage.
                // we won't be able to use the app if not connected for once. Afterwards, all data at least need to be written to
                //stacks of firestore.
                //so we just read

                walkDay = CloudProcessor.retrieveDay(LocalDate.now(), userEmail);
            }
            return false;
        }else{
            CloudProcessor.activateAccount(userEmail);
            Log.i(TAG, "linked user " +userEmail +" with UID: "+ currentUser.getUid() );
            //the first time case, and if never connected to internet, sync would be called again and again?
            //be shouldn't go to this if branch anymore since things are stored locally.


            //might need to iterate through a list of dates and call uploads
            //actually found that impossible since we only have cloud storage, each single walkday would be
            //inserted once created (at least on the write stack), when ever call sync, it should only upload 1 more day
            LocalDate d = LocalDate.now(); //....
            walkDay = new WalkDay(date.toString());
            CloudProcessor.uploadWalkDay(walkDay,userEmail);
            //upload other parts for the firstime as well like friend list or simply no.
            //most user info upload are implictly called in linkIdToEmail
            CloudProcessor.setLastUploadDate(d,userEmail);

            //then we start using the app.
            return true;
        }

    }

    public void init(){ //init now only reads and update view doesn't change/add data in the cloud even for firstime
        //sync called before init when first loading homepage but init could be called alone
        goal_today = walkDay.getGoal();
        goalMet = walkDay.getGoalMet();
        stepCountDailyReal = walkDay.getStepCountDailyReal();
        stepCountIntentionalReal = walkDay.getStepCountIntentionalReal();
        stepCountUnintentionalReal = walkDay.getStepCountUnintentionalReal();
        mock_steps_unintentional = walkDay.getMock_steps_unintentional();
        mock_steps_intentional = walkDay.getMock_steps_intentional();

        distanceDailyTotal =walkDay.getDistanceDaily();
        distanceRunTotal = walkDay.getDistanceRunTotal();
        time_elapsed_sec_daily = walkDay.getTime_run_sec_daily();

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
            Log.i(TAG,"Incorrect data computed for init");
        if(walkDay.getStepCountIntentional()-stepCountIntentionalTotal <-2 ||walkDay.getStepCountIntentional()-stepCountIntentionalTotal >2 )
            Log.i(TAG,"Incorrect data computed for init");
        updateHomePage();
        Log.i(TAG,"loaded today's data from shRef into HomePage");


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
        timeElapsedStr =computeTimeElapsed(); //computeTimeElapsed() was precise but have bug
        //time_elapsed_sec_run = timeRunMilli;
        Log.i(TAG,"timeElapsedStr "+timeElapsedStr);
        distanceRunTotal+=distance_delta+7;
        distanceRun += distance_delta+7;
        speed = (time_elapsed_sec_run) <= 1.f
                ?0.f
                :distanceRun*1000/time_elapsed_sec_run;
        average_speed =
                (time_elapsed_sec_daily == 0.f)
                        ? 0
                        : (distanceRunTotal *1000/ time_elapsed_sec_daily);
    }

    public String computeTime(){
        long second = (timeRunMilli / 1000) % 60;
        long minute = (timeRunMilli / (1000 * 60)) % 60;
        long hour = (timeRunMilli / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }




    public String computeTimeElapsed(){
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        time_elapsed_sec_run = cal.getTimeInMillis() - timeRunStart;

        Log.i(TAG,"time passed "+time_elapsed_sec_run);
        Log.i(TAG,"start time was "+timeRunStart);
        //long millis = time_elapsed % 1000;
        long second = (time_elapsed_sec_run / 1000) % 60;
        long minute = (time_elapsed_sec_run / (1000 * 60)) % 60;
        long hour = (time_elapsed_sec_run / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }//timeRunStart only initialized to non null if linkRun

    @Override
    public void update(Observable observable, Object object){

        //handles date__ changes
        /*
        if (!LocalDate.now().equals(date__) &&!timeTraveled){
            if(isRunning) runningMode.finish();
            date__ = LocalDate.now();
            init();
        }
        */

        Object[] arr = (Object[]) object;
        step_delta = (int)arr[1] - stepCountDailyReal; //needs to handle when past midnight
        stepCountDailyReal = (int)arr[1];
        distance_delta = (float)arr[2] - distanceDailyTotal;
        distanceDailyTotal = (float)arr[2];
        if(!isRunning){
            un_delta = step_delta;
            computeStep();


        }
        else{
            in_delta = step_delta;
            computeStep();
            if ((boolean)arr[3] == true){
                //timeRunMilli += 1000;
                computeStats();
            }
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
        runningMode.showGoal(goal_today);
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
            Log.i(TAG,"Incorrect data computed for cleanUpAfterRun");

        updateHomePage();
        time_elapsed_sec_daily += time_elapsed_sec_run;///needs modification
        time_elapsed_sec_run = 0;
        distanceRunTotal +=distanceRun;
        distanceRun =0;
        average_speed =
                (time_elapsed_sec_daily == 0.f)
                ? 0
                : (distanceRunTotal*1000 / time_elapsed_sec_daily);

        saveLocal();
    }

    public void unlinkRunning(){
        runningMode = null;
        isRunning = false;
        cleanUpAfterRun();
        timeRunStart =0;
    }


    //called onDestroy
    public void linkRunning(RunningMode rm){
        runningMode = rm;
        stepCountIntentionalBeforeRun =stepCountIntentionalReal;
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        timeRunStart = cal.getTimeInMillis();
        isRunning =true;
        computeStep();
        computeStats();
        updateRunningMode();
    }

    //calls cloud processor uploadWalkDay and setLastUpLoadDate
    public void saveLocal(){ //FIXME refactor the name maybe later

            walkDay.setStepCountUnintentionalReal(stepCountUnintentionalReal);
            walkDay.setStepCountUnintentional(stepCountUnintentionalTotal);//save but doesn't read in init
            walkDay.setStepCountIntentionalReal(stepCountIntentionalReal);
            walkDay.setStepCountIntentional(stepCountIntentionalTotal);//save but doesn't read in init
            walkDay.setStepCountDailyReal(stepCountDailyReal);
            walkDay.setStepCountDailyTotal(stepCountDailyTotal);//save but doesn't read in init
            walkDay.setDistanceDaily(distanceDailyTotal);
            walkDay.setDistanceRunTotal(distanceRunTotal);
            walkDay.setTime_run_sec_daily(time_elapsed_sec_daily);
            walkDay.setSpeed_average(average_speed);//save but doesn't read in init
            walkDay.setMock_steps_intentional(mock_steps_intentional+mock_steps_run);
            walkDay.setMock_steps_unintentional(mock_steps_unintentional);
            walkDay.setGoal(goal_today);
            walkDay.setGoalMet(goalMet);
            //dataProcessor.insertDay(date,walkDay);

            CloudProcessor.uploadWalkDay(walkDay,userEmail);
            CloudProcessor.setLastUploadDate(date,userEmail);


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


    public void timeTravelForward(){
        saveLocal();
        date =date.plusDays(1);
        walkDay = CloudProcessor.retrieveDay(date,userEmail);
        if(walkDay == null){
            CloudProcessor.uploadWalkDay(walkDay,userEmail);
        }
        //used to always insertDay, could be redundant,
        // it seems only need to insert when not found in storage

        init();//only change the view
    }


    public void timeTravelNow(){
        saveLocal();
        date =LocalDate.now();
        walkDay = CloudProcessor.retrieveDay(date,userEmail);
        if(walkDay == null){
            CloudProcessor.uploadWalkDay(walkDay,userEmail);
        }
        //used to always insertDay, could be redundant,
        // it seems only need to insert when not found in storage

        init();//only change the view
    }

    public void timeTravelBackward(){
        saveLocal();
        date = date.minusDays(1);
        walkDay = CloudProcessor.retrieveDay(date,userEmail);
        if(walkDay == null){
            CloudProcessor.uploadWalkDay(walkDay,userEmail);
        }
        //used to always insertDay, could be redundant,
        // it seems only need to insert when not found in storage

        init();//only change the view
    }

    public void mockStepInHP(){
        mock_steps_unintentional += 500;
        computeStep();
        updateHomePage();
        saveLocal();
    }

    public void mockStepInRM(){
        mock_steps_run += 500;
        computeStep();
        updateRunningMode();
        saveLocal();
    }

    public void setCurrentUser(FirebaseUser firebaseUser){
        currentUser = firebaseUser;

        userDisplayName= currentUser.getDisplayName();
        userEmail = currentUser.getEmail();
        if(currentUser == null)
            Log.d(TAG,"current user is null!");

        return;
    }


    public FirebaseUser getCurrentUser(){
        return currentUser;
    }

    public String getUserEmail(){
        return userEmail;
    }

    public String getUserDisplayName(){
        return userDisplayName;
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
        CloudProcessor.uploadWalkDay(walkDay,userEmail);
        init();
    }

    public boolean isFriend(String friendEmail){
        if (CloudProcessor.checkAisBFriend(userEmail,friendEmail)&&CloudProcessor.checkAisBFriend(friendEmail,userEmail))
            return true;
        else return false;

    }

    public boolean addFriend(String friendEmail){
        if(isFriend(friendEmail)) return false;
        else{
            CloudProcessor.aInviteB(userEmail,friendEmail);
            CloudProcessor.aAddB(userEmail,friendEmail);
            return true; //only indicate method success, doesn't mean friend accept invitation
        }
    }

}