package com.example.team10.personalbest;

import com.google.firebase.auth.FirebaseUser;

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

    boolean getGoalMet();
    void setGoalMet(boolean t);

    int getStepCountDailyTotal();
}
