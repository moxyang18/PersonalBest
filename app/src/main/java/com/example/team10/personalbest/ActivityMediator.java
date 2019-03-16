package com.example.team10.personalbest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.team10.personalbest.fitness.CloudProcessor;
import com.example.team10.personalbest.fitness.GoogleFitAdapter;
import com.example.team10.personalbest.friend.StringAsObject;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

public class ActivityMediator implements Observer, Mediator {

    protected static ActivityMediator instance;



    static private String TAG = "Mediator-";

    protected LocalDate date = LocalDate.now();
    private static WalkDay walkDay;
    private RunningMode runningMode;
    private HomePage homePage;
    private GoogleFitAdapter fit;

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
    private static String userEmail;
    private static String userDisplayName;

    //all the string of emails in this hashset should be friends
    //email.com is stored as email,com in cloud but here should be .com
    private static HashSet<String> friendList = new HashSet<>();

    private static int oneTimeShown = 0;
    private static int doubleShown = 0;
    private static int tripleShown = 0;

    //PersonalBestUser personalBestUser;

    public ActivityMediator(HomePage hp){
        homePage = hp;
        instance = this;
        //dataProcessor = new DataProcessor(homePage);
    }

    public static ActivityMediator getInstance(){
        return instance;
    }

    public static HashMap<String, WalkDay> getFriendWalkDays() {
        return friendWalkDays;
    }

    public static void setFriendWalkDays(HashMap<String, WalkDay> friendWalkDays) {
        ActivityMediator.friendWalkDays = friendWalkDays;
    }

    public static HashMap<String, WalkDay> getUserWalkDays() {
        return userWalkDays;
    }

    public static void setUserWalkDays(HashMap<String, WalkDay> userWalkDays) {
        ActivityMediator.userWalkDays = userWalkDays;
    }

    public static HashSet<String> getFriendList() {
        return friendList;
    }

    public static void setFriendList(HashSet<String> friendList) {
        ActivityMediator.friendList = friendList;
    }


    //we don't want to call sync too often, only upon potential difference between the local cache and cloud storage
    //we call sync whenever we reload the application
    public boolean sync(){
        if(CloudProcessor.checkExistingUserData(userEmail)){ //if not first time using the app

            Log.d(TAG, "revisiting user");
            StringAsObject updateInfo = CloudProcessor.getUpdateInfo(userEmail);
            //then check last upload date
            LocalDate lastInCloud = LocalDate.parse(updateInfo.getString1());
            //LocalDate today = LocalDate.now()
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
                CloudProcessor.setUpdateInfo(LocalDate.now(),LocalTime.now(),userEmail);
                Log.d(TAG, "Read in day from cloud");
            }

             //FIXME if we want to do actuall merge.
            //if same day, we only do things if walkDay currently is bad.
             else if(lastInCloud.isAfter(LocalDate.now())){ //couldn't happen
                Log.d(TAG, "Error: impossible");
                Log.d(TAG, lastInCloud.toString());
                Log.d(TAG, LocalDate.now().toString());
                walkDay = CloudProcessor.retrieveDay(LocalDate.now(), userEmail);
                if(walkDay ==null)
                    Log.d(TAG, "error, didn't read walkDay from cloud when loading HomePage");
            }else {//same day
                //lost phone today and get connected or we are just using the app
                //we can only get the day from cloud. can't rewrite it. since we don't actually local storage.
                // we won't be able to use the app if not connected for once. Afterwards, all data at least need to be written to
                //stacks of firestore.
                //so we just read
                //if(LocalTime.parse(updateInfo.getString2()))
                walkDay = CloudProcessor.retrieveDay(LocalDate.now(), userEmail);
                if(walkDay ==null)
                    Log.d(TAG, "error, didn't read walkDay from cloud when loading HomePage");
            }
            preloadUserWalkDays();
            CloudProcessor.loadFriendList(userEmail);
            CloudProcessor.checkFriendList(userEmail);
            return false;
        }else{
            Log.d(TAG, "First time user");
            CloudProcessor.activateAccount(userEmail);
            Log.d(TAG, "activated account with email: "+userEmail);
            Log.i(TAG, "linked user " +userEmail +" with UID: "+ currentUser.getUid() );
            //the first time case, and if never connected to internet, sync would be called again and again?
            //be shouldn't go to this if branch anymore since things are stored locally.


            //might need to iterate through a list of dates and call uploads
            //actually found that impossible since we only have cloud storage, each single walkday would be
            //inserted once created (at least on the write stack), 0w0hen ever call sync, it should only upload 1 more day
            LocalDate d = LocalDate.now(); //....
            walkDay = new WalkDay(date.toString());
            CloudProcessor.uploadWalkDay(walkDay,userEmail);
            //upload other parts for the firstime as well like friend list or simply no.
            //most user info upload are implictly called in linkIdToEmail
            CloudProcessor.setUpdateInfo(LocalDate.now(),LocalTime.now(),userEmail);

            //then we start using the app.
            preloadUserWalkDays();
            CloudProcessor.loadFriendList(userEmail);
            CloudProcessor.checkFriendList(userEmail);
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
        thresholdNotification();
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
            CloudProcessor.uploadWalkDay(walkDay,userEmail);
            CloudProcessor.setUpdateInfo(LocalDate.now(),LocalTime.now(),userEmail);


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
            walkDay = new WalkDay(date.toString());
            userWalkDays.put(date.toString(),walkDay);
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
            walkDay = new WalkDay(date.toString());
            userWalkDays.put(date.toString(),walkDay);
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
            walkDay = new WalkDay(date.toString());
            userWalkDays.put(date.toString(),walkDay);
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

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(homePage);
        String uEmail_2 = account.getEmail();
        if(!uEmail_2.equals(currentUser)){
            Log.d(TAG,"Conflicting current user from last signin GoogleSignIn is: "+uEmail_2+" FirebaseUser is: "+userEmail);
        }

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
        Log.d(TAG,"preloading friend walkdays");
        LocalDate dayDate2 ;
        for (int j = 0; j < 28; j++) {
            dayDate2= date.minusDays(j);
            CloudProcessor.requestDay(dayDate2,friendEmail,false);
        }
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

    //first parameter should always be user's email
    public static void addFriend(String userEmail,String friendEmail){
        CloudProcessor.aInviteB(userEmail,friendEmail);
    }

    public static void addInFriendList(String s){
        friendList.add(s);
        Log.d(TAG, "friendlist in AM is now: "+friendList.toString());
    }



    private void thresholdNotification() {
        String encourage_mes = "";
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        //WalkDay todayData = ActivityMediator.getUserWalkDays().get(today.toString());
        WalkDay yesterdayData = ActivityMediator.getUserWalkDays().get(yesterday.toString());

        int todayTotals = getStepCountDailyTotal();
        int yesterdayTotals = (yesterdayData==null) ? 0: yesterdayData.getStepCountDailyTotal();
        int increSteps = todayTotals-yesterdayTotals;
        double increment = 0;

        // display increment if there is any
        if(yesterdayTotals == 0 ){
            encourage_mes = "Awesome! You've increased your " +
                    "daily steps by " + increSteps + " steps.";
        }

        else {
            increment = todayTotals / yesterdayTotals * 1.0; //stepCount - yesterdayStepCount
            if (tripleShown == 0 && increment >= 3.0){
                encourage_mes = "Wonderful! You have tripled yesterday's goal!";
                tripleShown = 1;
            }
            else if (doubleShown == 0 && increment < 3.0 && increment >= 2.0) {
                encourage_mes = "Congratulations! You have doubled yesterday's goal!";
                doubleShown = 1;
            }
            else if (oneTimeShown == 0 && increment > 1.0 && increment < 2.0) {
                encourage_mes = "Awesome! You've increased your " +
                        "daily steps by " + increSteps + " steps.";
                oneTimeShown = 1;
            }
        }

        // create the notification manager and the channel
        NotificationManager notificationManager = (NotificationManager) homePage.getSystemService(Context.NOTIFICATION_SERVICE);
        String id = "threshold_message_chanel";
        CharSequence name = "threshold_encourage";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel encChannel = new NotificationChannel(id, name, importance);
        encChannel.enableLights(true);
        notificationManager.createNotificationChannel(encChannel);

        // build the local message sender
        NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(homePage, id)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Notification From PersonalBest Team")
                .setContentText(encourage_mes);

        if (increment >1.0)
            notificationManager.notify(1, notBuilder.build());
    }
}