package com.example.team10.personalbest;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.team10.personalbest.fitness.CloudProcessor;
import com.example.team10.personalbest.fitness.GoogleFitAdapter;
import com.example.team10.personalbest.friend.StringAsObject;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;

public class MockMediator implements Mediator,Observer {

    protected static MockMediator instance = null;
    private final int RC_SIGN_IN = 1;


    static private String TAG = "Mediator-";

    protected LocalDate date = LocalDate.now();
    private static WalkDay walkDay;
    private RunningMode runningMode;
    private HomePage homePage;
    private FakeFit fit;

    private static HashMap<String,WalkDay> userWalkDays = new HashMap<String,WalkDay>();
    private static HashMap<String,WalkDay> friendWalkDays = new HashMap<String,WalkDay>();

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
    public static String userEmail ="yad027@ucsd.edu";
    public static String userDisplayName="Yanzhi Ding";

    //all the string of emails in this hashset should be friends
    //email.com is stored as email,com in cloud but here should be .com
    private static HashSet<String> friendList = new HashSet<>();
    //protected FirebaseAuth firebaseAuth;

    public MockMediator(){
        instance = this;
    }
    public MockMediator(HomePage hp){
        homePage = hp;
        instance = this;
        //dataProcessor = new DataProcessor(homePage);
    }

    public static MockMediator getInstance(){
        return instance;
    }



    public static HashMap<String, WalkDay> getFriendWalkDays() {
        return friendWalkDays;
    }
    public HashMap<String, WalkDay> getFriendWalkDays2() {
        return friendWalkDays;
    }

    public static void setFriendWalkDays(HashMap<String, WalkDay> friendWalkDays) {
        MockMediator.friendWalkDays = friendWalkDays;
    }

    public HashMap<String, WalkDay> getUserWalkDays2() {
        return userWalkDays;
    }
    public static HashMap<String, WalkDay> getUserWalkDays() {
        return userWalkDays;
    }

    public static void setUserWalkDays(HashMap<String, WalkDay> userWalkDays) {
        MockMediator.userWalkDays = userWalkDays;
    }

    public HashSet<String> getFriendListByI(){
        return friendList;
    }
    public static HashSet<String> getFriendList() {
        return friendList;
    }

    public static void setFriendList(HashSet<String> friendList) {
        MockMediator.friendList = friendList;
    }


    @Override
    public void setUpFireApp(){

    }


    @Override
    public void GoogleCloudIntetnSend(){
        mockStartActivity();
    }

    public void startActivityForResult(Intent signInIntent){
        homePage.startActivityForResult( signInIntent, RC_SIGN_IN );
    }

    public void mockStartActivity(){
        //TODO
        sync();
        init();
        build();
        if (homePage !=null)
            homePage.runRunner();




    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

    }

    public void setHomePage(HomePage home){
        homePage = home;
    }

    //we don't want to call sync too often, only upon potential difference between the local cache and cloud storage
    //we call sync whenever we reload the application
    public boolean sync(){

        LocalDate d = LocalDate.now(); //....
        walkDay = userWalkDays.get(date.toString());
        if(walkDay == null) walkDay = new WalkDay(LocalDate.now().toString());
        return false;
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

        mock_steps_run = 0;
        distanceDailyTotal =walkDay.getDistanceDaily();
        distanceRunTotal = walkDay.getDistanceRunTotal();
        time_elapsed_sec_daily = walkDay.getTime_run_sec_daily();


        stepCountRun = 0;

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
        if(homePage!=null)updateHomePage();
        Log.i(TAG,"initialize Activity Mediator");


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
        if(homePage != null)
            updateHomePage();
        if(isRunning) updateRunningMode();

        if((boolean)arr[0] == true){
            saveLocal();
        }
    }

    public void updateRunningMode(){

        runningMode.showGoal(getGoal_today());
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
        //stepCountIntentionalReal +=stepCountRun;
        stepCountRun =0;
        stepCountIntentionalBeforeRun = stepCountIntentionalReal;
        stepCountIntentionalTotal = stepCountIntentionalReal + mock_steps_intentional;

        stepCountDailyTotal = stepCountUnintentionalTotal+stepCountIntentionalTotal;
        if(stepCountDailyTotal-stepCountDailyReal-mock_steps_unintentional-mock_steps_intentional >4 )
            Log.i(TAG,"Incorrect data computed for cleanUpAfterRun");

        if(homePage!=null) updateHomePage();
        time_elapsed_sec_daily += time_elapsed_sec_run;///needs modification
        time_elapsed_sec_run = 0;
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
        userWalkDays.put(date.toString(),walkDay);
    }

    public void build(){
        fit = new FakeFit();
        fit.addObserver(this);
    }

    public void stop(){
        fit.deleteObserver(this);
    }

    public void setup(){

    }


    public void timeTravelForward(){
        saveLocal();
        date =date.plusDays(1);
        walkDay = userWalkDays.get(date.toString());
        if(walkDay == null){
            walkDay = new WalkDay(date.toString());
            userWalkDays.put(date.toString(),walkDay);

        }
        //used to always insertDay, could be redundant,
        // it seems only need to insert when not found in storage

        init();//only change the view
    }


    public void timeTravelNow(){
        saveLocal();
        date =LocalDate.now();
        walkDay =userWalkDays.get(date.toString());
        if(walkDay == null){
            walkDay = new WalkDay(date.toString());
            userWalkDays.put(date.toString(),walkDay);
            //CloudProcessor.uploadWalkDay(walkDay,userEmail);
        }
        //used to always insertDay, could be redundant,
        // it seems only need to insert when not found in storage

        init();//only change the view
    }

    public void timeTravelBackward(){
        saveLocal();
        date = date.minusDays(1);
        walkDay = userWalkDays.get(date.toString());
        if(walkDay == null){
            walkDay = new WalkDay(date.toString());
            userWalkDays.put(date.toString(),walkDay);
            //CloudProcessor.uploadWalkDay(walkDay,userEmail);
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

    public void setGoal_today(int g){ goal_today = g; }

    public void setGoalMet(boolean m){
        goalMet =m;
    }

    public boolean getGoalMet(){
        return goalMet;
    }

    public int getStepCountDailyTotal(){
        return stepCountDailyTotal;
    }

    public void preloadUserWalkDays(){
        LocalDate dayDate;
        for (int i = 27; i >= 0; i--) {
            dayDate= date.minusDays(i);
            CloudProcessor.requestDay(dayDate,userEmail,true);
        }
    }
    public void preloadFriendWalkDays(String friendEmail){
    }

    public void resetDay(){
        walkDay = new WalkDay(LocalDate.now().toString());
        //CloudProcessor.uploadWalkDay(walkDay,userEmail);
        init();
    }


    //modified because all string in friendList are friends
    public static boolean isUsersFriend(String friendEmail){
        return friendList.contains(friendEmail);
    }

    public void addFriendByI(String userEmail,String friendEmail){
        friendList.add(friendEmail);
    }


    //first parameter should always be user's email
    public static void addFriend(String userEmail,String friendEmail){
        friendList.add(friendEmail);
    }

    public static void addInFriendList(String s){
        friendList.add(s);
        Log.d(TAG, "friendlist in AM is now: "+friendList.toString());
    }


    public static void reset(){

        instance = null;

        walkDay =null;
        HashMap<String,WalkDay> userWalkDays = new HashMap<String,WalkDay>();
        HashMap<String,WalkDay> friendWalkDays = new HashMap<String,WalkDay>();
        //all the string of emails in this hashset should be friends
        //email.com is stored as email,com in cloud but here should be .com
        HashSet<String> friendList = new HashSet<>();

    }
}
