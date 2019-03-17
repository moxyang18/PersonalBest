package com.example.team10.personalbest;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.HashSet;

public interface Mediator {

    void mockStepInHP();
    void mockStepInRM();
    void init();
    void setup();
    void stop();

    void linkRunning(RunningMode r);
    void unlinkRunning();

    boolean checkReachGoal();
    void saveLocal();
    void build();

    void setGoal_today(int i);
    int getGoal_today();
    void timeTravelBackward();
    void timeTravelNow();
    void timeTravelForward();

    void setCurrentUser(FirebaseUser u);

    boolean sync();
    String getUserEmail();
    boolean getGoalMet();
    void setGoalMet(boolean t);
    void preloadUserWalkDays();
    void preloadFriendWalkDays(String friendEmail);
    void addFriendByI(String userEmail,String friendEmail);

    void setUpFireApp();
    void GoogleCloudIntetnSend();
    void firebaseAuthWithGoogle(GoogleSignInAccount acct);

    int getStepCountDailyTotal();
    HashSet<String> getFriendListByI();
}
