package com.example.team10.personalbest;

import java.util.Observable;

public class FakeFit extends Observable {
    int steps = 0;
    float distance = 0;
    long timeElapsed = 0;
    String timeString = "";
    boolean onesecpassed = false;
    public static FakeFit instance;

    private Object result[]= {true, steps, distance, onesecpassed};

    public FakeFit(){
        instance = this;
    }

    public static FakeFit getInstance(){
        return  instance;
    }



    public void setStep(int s){
        result[1] = s;
        setChanged();
        notifyObservers(result);
    }

    public void setDistance(float meters){
        result[2] = meters;
        setChanged();
        notifyObservers(result);
    }

    public void setResult(Object[] arg){
        result = arg;
        setChanged();
        notifyObservers(result);
    }
}
